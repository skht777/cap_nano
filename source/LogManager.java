


public class LogManager {
	private static Logger logger;

	/**
	 * デバッグ用
	 */
	@Deprecated
	private static void initLogger() {
		logger = (message) -> {
			System.out.println(message);
		};
	}

	public static Logger getLogger() {
		if (logger == null)
			initLogger();
		return logger;
	}

	public static void setLogger(Logger newLogger) {
		logger = newLogger;
	}

	interface Logger {
		public void appendLog(String message);
	}
}

