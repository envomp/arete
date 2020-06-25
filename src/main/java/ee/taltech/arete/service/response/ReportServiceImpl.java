package ee.taltech.arete.service.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.taltech.arete.configuration.DevProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@EnableAsync
@Service
public class ReportServiceImpl implements ReportService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReportService.class);

	@Autowired
	private JavaMailSender javaMailSender;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	DevProperties devProperties;

	@Async
	@Override
	public void sendTextMail(String mail, String text, String header, Boolean html) {

		try {
			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
			helper.setFrom(devProperties.getAreteMail());
			helper.setTo(mail);
			helper.setSubject(header);
			helper.setText(text, html);
			javaMailSender.send(message);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	@Async
	@Override
	public void sendTextToReturnUrl(String returnUrl, String response) {
		try {
			post(returnUrl, response);
		} catch (IOException | InterruptedException e) {
			LOGGER.error("Failed to POST: {}", e.getMessage());
		}

	}

	private void post(String postUrl, String data) throws IOException, InterruptedException {

		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(postUrl))
				.POST(HttpRequest.BodyPublishers.ofString(data))
				.setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
				.build();

		client.send(request, HttpResponse.BodyHandlers.ofString());

	}
}
