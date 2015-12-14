package scaliper

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.Reader
import java.net.InetAddress
import java.net.UnknownHostException
import java.util.Collection
import java.util.regex.Matcher
import java.util.regex.Pattern

import scala.collection.JavaConverters._
import scala.collection.mutable

object Environment {

  def getSnapshot(): Map[String, String] = {
    val propertyMap = mutable.Map[String, String]()

    val sysProps = System.getProperties().asScala

    def putIfExists(propName: String, sysName: String) =
      sysProps.get(sysName) match {
        case Some(v) => propertyMap(propName) = v
        case _ =>
      }

    // Sometimes java.runtime.version is more descriptive than java.version
    sysProps.get("java.version") match {
      case Some(javaVersion) =>
        sysProps.get("java.runtime.version") match {
          case Some(alternateVersion) if alternateVersion.length > javaVersion.length =>
            propertyMap("jre.version") = alternateVersion
          case _ =>
            propertyMap("jre.version") = javaVersion
        }
        case _ =>
      }
          
    putIfExists("jre.vmname", "java.vm.name")
    putIfExists("jre.vmversion", "java.vm.version")
    propertyMap.put("jre.availableProcessors", Runtime.getRuntime().availableProcessors().toString)

    try {
      propertyMap("host.name") = InetAddress.getLocalHost.getHostName
    } catch {
      case _: UnknownHostException => 
    }

    sysProps.get("os.name") match {
      case Some(osName) =>
        propertyMap("os.name") = osName
        putIfExists("os.version", "os.version")
        putIfExists("os.arch", "os.arch")

        if(osName == "Linux") {
//          getLinuxEnvironment(propertyMap)
        }
      case None =>
    }

    return propertyMap.toMap
  }

  // private void getLinuxEnvironment(Map<String, String> propertyMap) {
  //   // the following probably doesn't work on ALL linux
  //   Multimap<String, String> cpuInfo = propertiesFromLinuxFile("/proc/cpuinfo");
  //   propertyMap.put("host.cpus", Integer.toString(cpuInfo.get("processor").size()));
  //   String s = "cpu cores";
  //   propertyMap.put("host.cpu.cores", describe(cpuInfo, s));
  //   propertyMap.put("host.cpu.names", describe(cpuInfo, "model name"));
  //   propertyMap.put("host.cpu.cachesize", describe(cpuInfo, "cache size"));

  //   Multimap<String, String> memInfo = propertiesFromLinuxFile("/proc/meminfo");
  //   // TODO redo memInfo.toString() so we don't get square brackets
  //   propertyMap.put("host.memory.physical", memInfo.get("MemTotal").toString());
  //   propertyMap.put("host.memory.swap", memInfo.get("SwapTotal").toString());

  //   getAndroidEnvironment(propertyMap);
  // }

  // private void getAndroidEnvironment(Map<String, String> propertyMap) {
  //   try {
  //     Map<String, String> map = getAndroidProperties();
  //     String manufacturer = map.get("ro.product.manufacturer");
  //     String device = map.get("ro.product.device");
  //     propertyMap.put("android.device", manufacturer + " " + device); // "Motorola sholes"

  //     String brand = map.get("ro.product.brand");
  //     String model = map.get("ro.product.model");
  //     propertyMap.put("android.model", brand + " " + model); // "verizon Droid"

  //     String release = map.get("ro.build.version.release");
  //     String id = map.get("ro.build.id");
  //     propertyMap.put("android.release", release + " " + id); // "Gingerbread GRH07B"
  //   } catch (IOException ignored) {
  //   }
  // }

  // private static String describe(Multimap<String, String> cpuInfo, String s) {
  //   Collection<String> strings = cpuInfo.get(s);
  //   // TODO redo the ImmutableMultiset.toString() call so we don't get square brackets
  //   return (strings.size() == 1)
  //       ? strings.iterator().next()
  //       : ImmutableMultiset.copyOf(strings).toString();
  // }

  // /**
  //  * Returns the key/value pairs from the specified properties-file like
  //  * reader. Unlike standard Java properties files, {@code reader} is allowed
  //  * to list the same property multiple times. Comments etc. are unsupported.
  //  */
  // private static Multimap<String, String> propertiesFileToMultimap(Reader reader)
  //     throws IOException {
  //   ImmutableMultimap.Builder<String, String> result = ImmutableMultimap.builder();
  //   BufferedReader in = new BufferedReader(reader);

  //   String line;
  //   while((line = in.readLine()) != null) {
  //     String[] parts = line.split("\\s*\\:\\s*", 2);
  //     if (parts.length == 2) {
  //       result.put(parts[0], parts[1]);
  //     }
  //   }
  //   in.close();

  //   return result.build();
  // }

  // private static Multimap<String, String> propertiesFromLinuxFile(String file) {
  //   try {
  //     Process process = Runtime.getRuntime().exec(new String[]{"/bin/cat", file});
  //     return propertiesFileToMultimap(
  //         new InputStreamReader(process.getInputStream(), "ISO-8859-1"));
  //   } catch (IOException e) {
  //     return ImmutableMultimap.of();
  //   }
  // }

  // public static void main(String[] args) {
  //   Environment snapshot = new EnvironmentGetter().getEnvironmentSnapshot();
  //   for (Map.Entry<String, String> entry : snapshot.getProperties().entrySet()) {
  //     System.out.println(entry.getKey() + " " + entry.getValue());
  //   }
  // }

  // /**
  //  * Android properties are available from adb shell /system/bin/getprop. That
  //  * program prints Android system properties in this format:
  //  * [ro.product.model]: [Droid]
  //  * [ro.product.brand]: [verizon]
  //  */
  // private static Map<String, String> getAndroidProperties() throws IOException {
  //   Map<String, String> result = new HashMap<String, String>();

  //   Process process = Runtime.getRuntime().exec(new String[] {"/system/bin/getprop"});
  //   BufferedReader reader = new BufferedReader(
  //       new InputStreamReader(process.getInputStream(), "ISO-8859-1"));

  //   Pattern pattern = Pattern.compile("\\[([^\\]]*)\\]: \\[([^\\]]*)\\]");
  //   String line;
  //   while ((line = reader.readLine()) != null) {
  //     Matcher matcher = pattern.matcher(line);
  //     if (matcher.matches()) {
  //       result.put(matcher.group(1), matcher.group(2));
  //     }
  //   }
  //   return result;
  // }
}
