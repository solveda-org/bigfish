package common;

import java.sql.Timestamp

import javolution.util.FastList
import javolution.util.FastMap
import com.osafe.util.Util;

import org.ofbiz.base.util.*
import org.ofbiz.base.util.string.*

latestVersion = null;
lastBuiltDate = null;
FileExt = "doc";

osafeProperties = UtilProperties.getResourceBundleMap("OsafeProperties.xml", locale);
versionFilePath = FlexibleStringExpander.expandString(osafeProperties.bigfishVersionFilesPath, context);
deploymentPropertyFile = FlexibleStringExpander.expandString(osafeProperties.bigfishDeploymentPropertyFile, context);

fileList = FileUtil.findFiles(FileExt, versionFilePath, null, null);
if(UtilValidate.isNotEmpty(fileList))
{

	 // Get the latest release name
     releaseFileList = FastList.newInstance();
     for (releaseFile in fileList)
     {
         if (releaseFile.getName().startsWith("BF-Version-Release-V"))
         {
             infoMap = FastMap.newInstance();
             infoMap.put("file", releaseFile);
             infoMap.put("fileName", releaseFile.getName());
             infoMap.put("fileNameUpperCase", releaseFile.getName().toUpperCase());
             String releaseFileName = releaseFile.getName();
             releaseFileName = releaseFileName.replaceAll("BF-Version-Release-V", "");
             releaseFileName = releaseFileName.replaceAll("doc", "");
             releaseFileNameArr = releaseFileName.split("\\.");
             
             String sortKey = "";
             for(String releaseFileNamePart : releaseFileNameArr)
             {
             	if(releaseFileNamePart.length() < 2)
             	{
             		sortKey = sortKey + "0";
             	}
             	sortKey = sortKey + releaseFileNamePart;
             }
             infoMap.put("sortKey", Integer.parseInt(sortKey));
             releaseFileList.add(infoMap);
         }
     }
	 if(UtilValidate.isNotEmpty(releaseFileList))
	 {
	     releaseFileList = UtilMisc.sortMaps(releaseFileList, ["sortKey"]);
		 latestReleaseFile = releaseFileList.first();
		 if (releaseFileList.size() > 1)
		 {
			 latestReleaseFile = releaseFileList.get(releaseFileList.size()-1);
		 }
	     latestReleaseFile = latestReleaseFile.fileName.replaceFirst("BF-Version-Release-V", "");
		 if(UtilValidate.isNotEmpty(latestReleaseFile))
		 {
			 latestRelease = latestReleaseFile.substring(0, latestReleaseFile.lastIndexOf(FileExt)-1);
		 }
	 }

	 // Get the last build date
	 deploymentPropertyFile = FileUtil.getFile(deploymentPropertyFile);
	 if (deploymentPropertyFile.exists())
	 {
	     lastBuiltDate = new Timestamp(deploymentPropertyFile.lastModified());
	     lastBuiltDate = Util.convertDateTimeFormat(lastBuiltDate, globalContext.preferredDateTimeFormat);

	 }
}
context.latestVersion = latestVersion;
context.latestRelease = latestRelease;
context.lastBuiltDate = lastBuiltDate;

