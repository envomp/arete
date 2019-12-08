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

import java.util.ArrayList;
import java.util.List;


@Service
public class SubmissionServiceImpl implements SubmissionService {

	private static final Logger LOG = LoggerFactory.getLogger(SubmissionService.class);
	@Autowired
	private ObjectMapper jacksonObjectMapper;
	@Autowired
	private SubmissionRepository submissionRepository;

	private static String getRandomHash() {
		return RandomStringUtils.random(40, true, true).toLowerCase();
	}

	@Override
	public void populateAsyncFields(Submission submission) {
		if (submission.getPriority() == null) {
			submission.setPriority(5);
		}

		if (submission.getTimestamp() == null) {
			submission.setTimestamp(System.currentTimeMillis());
		}

		if (submission.getDockerExtra() == null) {
			submission.setDockerExtra(new String[]{"stylecheck"});
		}

		if (submission.getUniid() == null) {
			String[] url = submission.getGitStudentRepo().split("/");
			submission.setUniid(url[url.length - 2]);
		}

		if (submission.getProject() == null) {
			String[] url = submission.getGitStudentRepo().split("/");
			submission.setProject(url[url.length - 1]);
		}

		if (submission.getDockerTimeout() == null) {
			submission.setDockerTimeout(120); // 120 sec
		}

		if (submission.getSystemExtra() == null) {
			submission.setSystemExtra(new String[]{});
		}
	}

	@Override
	public String populateSyncFields(Submission submission) {
		String hash;

		if (submission.getHash() == null) {
			hash = getRandomHash();
			submission.setHash(hash);
			submission.setReturnUrl(String.format("http://localhost:8098/waitingroom/%s", hash));
		} else {
			hash = submission.getHash(); //For integration test only.
		}

		if (submission.getPriority() == null) {
			submission.setPriority(5);
		}

		if (submission.getTimestamp() == null) {
			submission.setTimestamp(System.currentTimeMillis());
		}

		if (submission.getDockerExtra() == null) {
			submission.setDockerExtra(new String[]{"stylecheck"});
		}

		if (submission.getUniid() == null) {
			submission.setUniid("Codera");
		}

		if (submission.getSlugs() == null) {
			String path = submission.getSource()[0].getPath().split("\\\\")[0];
			submission.setSlugs(new String[]{path});
		}

		if (submission.getProject() == null) {
			String[] url = submission.getGitTestSource().split("/");
			submission.setProject(url[url.length - 2]);
		}

		if (submission.getDockerTimeout() == null) {
			submission.setDockerTimeout(120); // 120 sec
		}

		if (submission.getSystemExtra() == null) {
			submission.setSystemExtra(new String[]{"noMail"});
		}

		return hash;
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
}
