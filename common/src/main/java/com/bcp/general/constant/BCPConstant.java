package com.bcp.general.constant;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public interface BCPConstant {

	public final static int DEFAULTKAPPA = 256;
	public final static String DEFAULTKAPPASTR = String.valueOf(DEFAULTKAPPA);
	
	public final static int DEFAULTCERTAINTY = 100;
	public final static String DEFAULTCERTAINTYSTR = String.valueOf(DEFAULTCERTAINTY);

	public final static String BCP_FILE_EXTENSION = "bcp";

	public final static String DEFAULTCHARSET = Charset.defaultCharset().toString();

	public final static String[] membersname = { "kappa", "certainty", "n", "k", "g", "mp", "mq", "p", "q" };

	public final static Set<String> membersnameset = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(membersname)));

}
