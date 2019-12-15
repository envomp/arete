package ee.taltech.arete.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import ee.taltech.arete.api.data.response.arete.File;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@ToString
@Entity
@Getter
@Setter
@Builder()
@Table(name = "submission")
public class Submission {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@NotNull
	private String testingPlatform;

	private String returnUrl;

	private String gitStudentRepo;
	//  or
	@Transient
	private File[] source;

	private String hash;

	private String uniid;

	private String project;

	@Column(length = 16383)
	private String[] slugs;

	@JsonIgnore
	@Column(columnDefinition = "TEXT")
	private String result;

	private String[] dockerExtra;
	private String[] systemExtra;
	private Integer dockerTimeout;

	private Long timestamp;

	private Integer priority;
	private Integer thread;

	public Submission() {
	}


	public Submission(long id, String testingPlatform, String returnUrl, String gitStudentRepo, File[] source, String hash, String uniid, String project, String[] slugs, String result, String[] dockerExtra,
	                  String[] systemExtra, Integer dockerTimeout, Long timestamp, Integer priority, Integer thread) {
		this.testingPlatform = testingPlatform;
		this.returnUrl = returnUrl;
		this.gitStudentRepo = gitStudentRepo;
		this.source = source;
		this.hash = hash;
		this.uniid = uniid;
		this.project = project;
		this.slugs = slugs;
		this.result = result;
		this.dockerExtra = dockerExtra;
		this.systemExtra = systemExtra;
		this.dockerTimeout = dockerTimeout;
		this.timestamp = timestamp;
		this.priority = priority;
		this.thread = thread;
	}
}
