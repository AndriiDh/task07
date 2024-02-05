package com.task07;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.syndicate.deployment.annotations.events.RuleEventSource;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.resources.DependsOn;
import com.syndicate.deployment.model.ResourceType;
import org.joda.time.DateTime;

import java.util.*;

@LambdaHandler(lambdaName = "uuid_generator", roleName = "uuid_generator-role")
@RuleEventSource(targetRule = "uuid_trigger")
@DependsOn(name = "uuid_trigger", resourceType = ResourceType.CLOUDWATCH_RULE)
public class UuidGenerator implements RequestHandler<Object, String> {
	private final AmazonS3 amazonS3;
	private final Gson jsonFormatter;

	public UuidGenerator() {
		this.amazonS3 = AmazonS3ClientBuilder
				.standard()
				.withRegion("eu-central-1")
				.build();
		this.jsonFormatter = new GsonBuilder().create();
	}

	public String handleRequest(Object request, Context context) {
		UuidDto uuid = new UuidDto();
		uuid.setIds(generateUUID(10));
		String fileName = DateTime.now().toString();
		amazonS3.putObject("cmtr-4a1bbc9a-uuid-storage-test", fileName, jsonFormatter.toJson(uuid));
		return "success";
	}

	private static List<String> generateUUID(int number) {
		List<String> ids = new ArrayList<>();
		for (int i = 0; i < number; i++) {
			ids.add(UUID.randomUUID().toString());
		}
		return ids;
	}

	public class UuidDto {
		private List<String> ids;

		public List<String> getIds() {
			return ids;
		}

		public void setIds(List<String> ids) {
			this.ids = ids;
		}

	}


}
