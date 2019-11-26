package ee.taltech.arete.service.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.core.command.PullImageResultCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

class ImageCheck {

	private static Logger LOGGER = LoggerFactory.getLogger(ImageCheck.class);

	private boolean myResult;
	private DockerClient dockerClient;
	private String image;
	private Image tester;

	ImageCheck(DockerClient dockerClient, String image) {
		this.dockerClient = dockerClient;
		this.image = image;
	}

	boolean is() {
		return myResult;
	}

	boolean pull() throws InterruptedException {
		LOGGER.info("Pulling new image: {}", image);
		return dockerClient.pullImageCmd(image)
				.exec(new PullImageResultCallback())
				.awaitCompletion(300, TimeUnit.SECONDS);
	}

	Image getTester() {
		return tester;
	}

	void invoke() {
		List<Image> images = dockerClient.listImagesCmd().withShowAll(true).exec();

		for (Image tester : images) {
			ImageCheck.this.tester = tester;
			for (String tag : tester.getRepoTags()) {
				if (tag.equals(image)) {
					myResult = true;
					return;
				}
			}
		}
		myResult = false;
	}
}