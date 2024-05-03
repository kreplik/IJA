package ija.example;

import ija.common.Environment;
import ija.common.Robot;
import ija.room.ControlledRobot;
import ija.room.Room;
import ija.tool.EnvPresenter;
import ija.tool.common.Position;
import javafx.application.Application;

import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main {

	private static Map<String, Map<String, Integer>> parameters;

	public Map<String, Map<String, Integer>> getParams(){
		return parameters;
	}

	public static void main(String... args) {

		Application.launch(EnvPresenter.class);

	}

/*
		int listsize = autorobots.size();

		while (true) {
			for(int i = 0; i < listsize; i++) {
				if (!autorobots.get(i).canMove()) {
					autorobots.get(i).turn();
					System.out.println("Autonomous Robot: " + i + " turned");
				}
				autorobots.get(i).move();
				System.out.println("Autonomous Robot: " + i + " moved");
			}
			sleep(1000);
		}
	}

	public static void sleep(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException ignored) {

		}
		*/

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

