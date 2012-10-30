
import java.math.BigInteger;
import javax.crypto.spec.*;

public class Skip 
{
	// skip's 1024 DH parameters
	private static final String skip1024String =
		"F488FD584E49DBCD20B49DE49107366B336C380D451D0F7C88B31C7C5B2D8EF6" +
		"F3C923C043F0A55B188D8EBB558CB85D38D334FD7C175743A31D186CDE33212C" +
		"B52AFF3CE1B1294018118D7C84A70A72D686C40319C807297ACA950CD9969FAB" +
		"D00A509B0246D3083D66A45D419F9C7CBD894B221926BAABA25EC355E92F78C7";

	// Modulus
	private static final BigInteger skip1024Modulus = new BigInteger(skip1024String, 16);

	// Base
	private static final BigInteger skip1024Base = BigInteger.valueOf(2);

	public static final DHParameterSpec sDHParameterSpec = new DHParameterSpec(skip1024Modulus, skip1024Base);
}