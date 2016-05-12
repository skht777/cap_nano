import java.util.Optional;

public class LogManager{
	private static Logger logger;
	/**
	 * デバッグ用
	 */
	@Deprecated
	private static Logger initLogger(){
		setLogger(System.out::println);
		return logger;
	}
	public static Logger getLogger(){return Optional.of(logger).orElse(initLogger());}
	public static void setLogger(Logger newLogger){logger = newLogger;}
	public interface Logger{
		public void appendLog(String message);
	}
}

