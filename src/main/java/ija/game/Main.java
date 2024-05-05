package ija.game;

import ija.tool.EnvPresenter;
import javafx.application.Application;

import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {

	private static Map<String, Map<String, Integer>> parameters;

	public Map<String, Map<String, Integer>> getParams(){
		return parameters;
	}

	public static void main(String... args) {

		Application.launch(EnvPresenter.class);


	}

	public static class ParametersLoader {
		private final String filePath;

		public ParametersLoader(String filePath) {
			this.filePath = filePath;
		}

		public Map<String, Map<String, Integer>> loadParameters() throws IOException {
			Map<String, Map<String, Integer>> parameters = new HashMap<>();
			String currentSection = null;


			try (BufferedReader reader = new BufferedReader(new FileReader(this.filePath))) {
				String line;
				while ((line = reader.readLine()) != null) {
					line = line.trim();
					if (line.startsWith("[")) {
						currentSection = line.substring(1, line.length() - 1);
						parameters.put(currentSection, new HashMap<>());
					} else if (currentSection != null && !line.isEmpty()) {
						String[] parts = line.split("=");
						String key = parts[0].trim();
						Integer value = Integer.valueOf(parts[1].trim());
						parameters.get(currentSection).put(key, value);
					}
				}
			}

			return parameters;
		}

	}
}

