package ee.taltech.arete.service.submission;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.taltech.arete.domain.Submission;
import ee.taltech.arete.exception.RequestFormatException;
import ee.taltech.arete.repository.SubmissionRepository;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.ws.rs.BadRequestException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;


@Service
public class SubmissionServiceImpl implements SubmissionService {

	private static final Logger LOG = LoggerFactory.getLogger(SubmissionService.class);
	@Autowired
	private ObjectMapper jacksonObjectMapper;
	@Autowired
	private SubmissionRepository submissionRepository;

	private Boolean DEBUG = true;

	private static String getRandomHash() {
		return RandomStringUtils.random(40, true, true).toLowerCase();
	}

	@Override
	public void populateAsyncFields(Submission submission) {

		String repo; //set OtherDefaults
		if (submission.getGitStudentRepo() == null) {
			throw new BadRequestException("Git student repo is needed for async.");
		} else {
			fixRepo(submission);
			repo = submission.getGitStudentRepo().replaceAll(".git", "");
		}

		if (submission.getUniid() == null) {
			String[] url = repo.split("[/:]");
			submission.setUniid(url[url.length - 2]);
		}

		if (submission.getFolder() == null) {
			String[] url = repo.split("[/:]");
			submission.setFolder(url[url.length - 1]);
		}

		if (submission.getCourse() == null) {
			if (repo.contains("/exams/")) {
				// in case of exams, the course name is the string before "exams" path
				String[] url = repo.split("[/:]");
				submission.setCourse(url[url.length - 3]);
			} else {
				String[] url = repo.split("[/:]");
				submission.setCourse(url[url.length - 1]);
			}
		}

		populateDefaultValues(submission);

	}

	@Override
	public String populateSyncFields(Submission submission) {
		String hash;

		if (submission.getSource() == null || submission.getSource().size() == 0) {
			throw new BadRequestException("Source is needed for sync testing.");
		}

		fixRepo(submission);

		if (submission.getHash() == null) {
			hash = getRandomHash();
			submission.setHash(hash);
			submission.setReturnUrl(String.format("http://localhost:8098/waitingroom/%s", hash));
		} else {
			hash = submission.getHash(); //For integration test only.
		}

		if (submission.getUniid() == null) {
			submission.setUniid("Codera");
		}

		if (submission.getSlugs() == null) {
			String path = submission.getSource().get(0).getPath().split("\\\\")[0];
			if (path.equals(submission.getSource().get(0).getPath())) {
				path = submission.getSource().get(0).getPath().split("/")[0];
			}
			submission.setSlugs(new HashSet<>(Collections.singletonList(path)));
		}

		if (submission.getFolder() == null) {
			String[] url = submission.getGitTestSource().split("[/:]");
			submission.setFolder(url[url.length - 2]);
		}

		if (submission.getCourse() == null) {
			String[] url = submission.getGitTestSource().split("[/:]");
			submission.setCourse(url[url.length - 2]);

		}

		populateDefaultValues(submission);

		submission.getSystemExtra().add("noMail");

		return hash;
	}

	@Override
	public void fixRepo(Submission submission) {
		if (submission.getGitStudentRepo() != null) {
			submission.setGitStudentRepo(fixRepository(submission.getGitStudentRepo()));
		}

		if (submission.getGitTestSource() != null) {
			submission.setGitTestSource(fixRepository(submission.getGitTestSource()));
		}

	}

	@Override
	public String fixRepository(String url) {
		if (System.getenv().containsKey("GITLAB_PASSWORD")) {
			if (url.startsWith("git")) {
				url = url.replaceFirst(":", "/");
				url = url.replace("git@", "https://");
			}

		} else {
			if (url.startsWith("http")) {
				url = url.replace("https://", "git@");
				url = url.replaceFirst("/", ":");
			}
		}
		if (!url.endsWith(".git")) {
			return url + ".git";
		}
		return url;
	}

	private void populateDefaultValues(Submission submission) {
		if (submission.getPriority() == null) {
			submission.setPriority(5);
		}

		if (submission.getTimestamp() == null) {
			submission.setTimestamp(System.currentTimeMillis());
		}

		if (submission.getDockerTimeout() == null) {
			if (DEBUG) {
				submission.setDockerTimeout(360); // 360 sec
			} else {
				submission.setDockerTimeout(120); // 120 sec
			}
		}

		if (submission.getDockerExtra() == null) {
			submission.setDockerExtra(new HashSet<>());
			if (DEBUG) {
				submission.getDockerExtra().add("stylecheck");
			}
		}

		if (submission.getSystemExtra() == null) {
			submission.setSystemExtra(new HashSet<>());
		}
	}

	@Override
	public List<Submission> getSubmissions() {
		LOG.info("Reading all Submissions from database.");
		return submissionRepository.findAll();
	}

	@Override
	public List<Submission> getSubmissionByHash(String hash) {
		ArrayList<Submission> submissions = submissionRepository.findByHash(hash);
		LOG.info("Reading Submission hash " + hash + " from database.");
		if (submissions.size() > 0) {
			return submissions;
		}
		LOG.error(String.format("Submission with hash %s was not found.", hash));
		throw new RequestFormatException(String.format("No Submission with hash: %s was not found", hash));
	}

	@Override
	public void saveSubmission(Submission submission) {
		submissionRepository.saveAndFlush(submission);
		LOG.info("Submission with hash {} successfully saved into DB", submission.getHash());
	}

	@Override
	@Scheduled(cron = "0 4 4 * * ?")
	public void deleteSubmissionsAutomatically() {
//		for (Submission submission : submissionRepository.findAll()) {
//			if (System.currentTimeMillis() - submission.getTimestamp() > (1000 * 60 * 60 * 24 * 7)) { // if it has been a week
//				submissionRepository.delete(submission);
//				LOG.info("Deleted old submission from DB: {}", submission);
//			}
//		}
	}

	@Override
	public void debugMode(boolean bool) {
		this.DEBUG = bool;
	}

	@Override
	public boolean isDebug() {
		return DEBUG;
	}

}
