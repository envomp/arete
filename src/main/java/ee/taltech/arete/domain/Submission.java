package ee.taltech.arete.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import ee.taltech.arete.api.data.SourceFile;
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
	private String gitTestSource;
	@NotNull
	private String testingPlatform;

	private String returnUrl;

	private String gitStudentRepo;
	//  or
	@Transient
	private SourceFile[] source;

	private String hash;

	private String uniid;

	private String project;

	@Column(length = 16383)
	private String[] slugs;

	@JsonIgnore
	@Transient
	private final StringBuilder result = new StringBuilder();

	@JsonIgnore
	@Column(columnDefinition = "TEXT")
	private String resultTest;

	private String[] dockerExtra;
	private String[] systemExtra;
	private Integer dockerTimeout;

	private Long timestamp;

	private Integer priority;
	private Integer thread;

	public Submission() {
	}


	public Submission(long id, String gitTestSource, String testingPlatform, String returnUrl, String gitStudentRepo, SourceFile[] source, String hash, String uniid, String project, String[] slugs, String resultTest, String[] dockerExtra,
	                  String[] systemExtra, Integer dockerTimeout, Long timestamp, Integer priority, Integer thread) {
		this.gitTestSource = gitTestSource;
		this.testingPlatform = testingPlatform;
		this.returnUrl = returnUrl;
		this.gitStudentRepo = gitStudentRepo;
		this.source = source;
		this.hash = hash;
		this.uniid = uniid;
		this.project = project;
		this.slugs = slugs;
		this.resultTest = resultTest;
		this.dockerExtra = dockerExtra;
		this.systemExtra = systemExtra;
		this.dockerTimeout = dockerTimeout;
		this.timestamp = timestamp;
		this.priority = priority;
		this.thread = thread;
	}
}
