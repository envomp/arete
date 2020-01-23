package ee.taltech.arete.api.data.response.arete;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "unit_test")
@Entity
@JsonClassDescription("Unit test")
public class UnitTest {

	@JsonPropertyDescription("Groups of unittests this unittest depends on. If any test fails in that group, this test is skipped")
	@ElementCollection
	@CollectionTable(name = "depended_groups", joinColumns = @JoinColumn(name = "id"))
	@Column(length = 1023)
	List<String> groupsDependedUpon;

	@Column(length = 1023)
	@JsonPropertyDescription("Status of the unittest")
	TestStatus status;

	enum TestStatus {
		PASSED,
		FAILED,
		SKIPPED
	}

	@JsonPropertyDescription("Test weight")
	Integer weight;

	@JsonPropertyDescription("Boolean whether to show exception message to student or not")
	Boolean printExceptionMessage;

	@JsonPropertyDescription("Boolean whether to show stack trace to student or not")
	Boolean printStackTrace;

	@Column(length = 1023)
	@JsonPropertyDescription("Time spent on test")
	Long timeElapsed;

	@JsonPropertyDescription("Methods depended, otherwise skipped")
	@ElementCollection
	@CollectionTable(name = "depended_methods", joinColumns = @JoinColumn(name = "id"))
	@Column(length = 1023)
	List<String> methodsDependedUpon;

	@Column(columnDefinition = "TEXT")
	@JsonPropertyDescription("Stacktrace")
	String stackTrace;

	@Column(length = 1023)
	@JsonPropertyDescription("Test name")
	String name;

	@JsonPropertyDescription("List of stdouts")
	@OneToMany(cascade = {CascadeType.ALL})
	List<ConsoleOutput> stdout;

	@Column(length = 1023)
	@JsonPropertyDescription("Exception class")
	String exceptionClass;

	@JsonPropertyDescription("Exception message")
	@Column(columnDefinition = "TEXT")
	String exceptionMessage;

	@JsonPropertyDescription("List of stderrs")
	@OneToMany(cascade = {CascadeType.ALL})
	List<ConsoleOutput> stderr;

	@JsonIgnore
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

}
