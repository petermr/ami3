package org.contentmine.ami.tools;

import picocli.CommandLine;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@link CommandLine.IVersionProvider} implementation that returns version information
 * from the ami-x.x.jar file's {@code /META-INF/MANIFEST.MF} file.
 * If not found, it tries to obtain the version from the `pom.xml` file in the current
 * directory, specifically from the first line containing `<version>` in that file.
 */
public class ManifestVersionProvider implements CommandLine.IVersionProvider {

	public String[] getVersion() throws Exception {
		Enumeration<URL> resources = ManifestVersionProvider.class.getClassLoader().getResources("META-INF/MANIFEST.MF");
		Object manifestVersion = null;
		URL url = null;
		while (resources.hasMoreElements()) {
			url = resources.nextElement();
			try {
				// there are many JAR files in the classpath containing a "META-INF/MANIFEST.MF" file
				// we need to loop through them until we find the one we are interested in.
				Manifest manifest = new Manifest(url.openStream());
				if (isApplicableManifest(manifest)) {
					Attributes attr = manifest.getMainAttributes();
					manifestVersion = get(attr, "Implementation-Version");
					break;
				}
			} catch (IOException ex) {
				return new String[] { "Unable to read from " + url + ": " + ex };
			}
		}
		String version = manifestVersion == null ? readPomVersion() : String.format("@|bold ${COMMAND-FULL-NAME} %s|@%n(%s)", manifestVersion, shrink(url));
		return new String[] {
				version,
				"JVM: ${java.version} (${java.vendor} ${java.vm.name} ${java.vm.version})",
				"OS: ${os.name} ${os.version} ${os.arch}"
		};
	}

	private String shrink(URL url) {
		if (url == null) {
			return null;
		}
		String s = url.toString();
		int pos = s.indexOf('!');
		return pos < 0 ? s : s.substring(0, pos);
	}

	private String readPomVersion() throws IOException {
		Path pom = Paths.get("pom.xml");
		if (Files.exists(pom)) {
			try (Scanner scanner = new Scanner(pom)) {
				while (scanner.hasNextLine()) {
					String line = scanner.nextLine();
					if (line.contains("<version>")) {
						Pattern versionPattern = Pattern.compile(".*<version>(.*)</version>");
						Matcher matcher = versionPattern.matcher(line);
						if (matcher.matches()) {
							return String.format("@|bold ${COMMAND-FULL-NAME} %s|@%n(%s)", matcher.group(1), pom.toAbsolutePath());
						}
						return "${COMMAND-FULL-NAME} " + line + "%n" + pom.toAbsolutePath();
					}
				}
			}
		}
		return "${COMMAND-FULL-NAME} UNKNOWN"; // pom.xml not found
	}

	private boolean isApplicableManifest(Manifest manifest) {
		Attributes attributes = manifest.getMainAttributes();
		return "ami3".equals(get(attributes, "Implementation-Title"));
	}

	private static Object get(Attributes attributes, String key) {
		return attributes.get(new Attributes.Name(key));
	}
}
