import soot.BodyTransformer;
import soot.Pack;
import soot.PackManager;
import soot.Scene;
import soot.SootClass;
import soot.SootResolver;
import soot.Transform;
import soot.options.Options;

public class driver {
	
	private static final BodyTransformer analysis = new wrapperclass();
	private static final String SRC_DIR = "F:\\workspace\\PA_Project\\src;";

	/**
	 * Main function to run to start the analysis.
	 * 
	 * @param args such that args[0] is the test class.
	 *            
	 */
	public static void main(String[] args) {
		// Check the length of the arguments.
		if (args.length < 1) {
			throw new IllegalArgumentException(
					"The name of the test class was not provided.");
		}

		/*// Add a new phase.
		PackManager.v().getPack("jtp").add(new Transform("jtp.instrumenter", driver.analysis));

		// Set options to keep the original variable names, output Jimple, use
		// the Java source and update the class path.
		Options.v().setPhaseOption("jb", "use-original-names");
		Options.v().set_output_format(Options.output_format_J);
		Options.v().set_src_prec(Options.src_prec_java);
		Scene.v().setSootClassPath(
				driver.SRC_DIR + Scene.v().getSootClassPath()); 

		// Load the given class.
		SootClass class_analyze = Scene.v().loadClassAndSupport(args[0]);

		// Get the class name.
		System.out.println("Loaded Class: " + class_analyze.getName() + "\n");

		// Start the analysis.
		soot.Main.main(args);*/
		
		SootClass abc=Scene.v().loadClassAndSupport("Test_programs.example1_original");
		//abc.setApplicationClass();
		SootClass xyz = Scene.v().loadClassAndSupport("Test_programs.example1_plagiarised");
		//xyz.setApplicationClass();
		Options.v().setPhaseOption("jb", "use-original-names:true");
		Pack jtp=PackManager.v().getPack("jtp");
		Pack jtp_new=PackManager.v().getPack("jtp_new");
		jtp.add(new Transform("jtp.instrumenter", new wrapperclass()));
		//jtp_new.add(new Transform("jtp.instrumenter", new wrapperclass_new()));
		
		SootResolver.v().resolveClass("java.lang.CloneNotSupportedException", SootClass.SIGNATURES);
		Options.v().set_output_format(Options.output_format_jimple);
		soot.Main.main(args);
	}
}
