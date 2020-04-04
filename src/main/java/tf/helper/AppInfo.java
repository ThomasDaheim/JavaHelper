/*
 * Copyright (c) 2014ff Thomas Feuster
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package tf.helper;

import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hold info on the running up - either through setting or through reading jar file
 * @author thomas
 */
public class AppInfo {
    private final static AppInfo INSTANCE = new AppInfo();
    
    private static final String UNKNOWN = "unknown";
    
    private String appName = UNKNOWN;
    private String appVersion = UNKNOWN;
    private String appURL = UNKNOWN;
    private String builtBy = UNKNOWN;
    private String buildTimestamp = UNKNOWN;
    private String createdBy = UNKNOWN;
    private String buildJdk = UNKNOWN;
    private String buildOS = UNKNOWN;

    private AppInfo() {
        super();
    }
    
    public static AppInfo getInstance() {
        return INSTANCE;
    }
    
    public String getAppName() {
        return appName;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public String getAppURL() {
        return appURL;
    }

    public String getBuiltBy() {
        return builtBy;
    }

    public String getBuildTimestamp() {
        return buildTimestamp;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getBuildJdk() {
        return buildJdk;
    }

    public String getBuildOS() {
        return buildOS;
    }

    public void initAppInfo(final Class<?> klass, final String inAppName, final String inAppVersion, final String inAppURL) {
        appName = inAppName;
        appVersion = inAppVersion;
        appURL = inAppURL;

        // TFE, 20190823: try to read data from manifest file
        try {
	    final JarFile jar = JarFileLoader.jarFileOf(klass);
//            System.out.println("jar: " + jar);
            if (jar != null) {
                final Manifest manifest = jar.getManifest();
                final Attributes manifestContents = manifest.getMainAttributes();

                appName = setFromAttributesIfNotNull(appName, manifestContents, "App-Name");
                appVersion = setFromAttributesIfNotNull(appVersion, manifestContents, "App-Version");
                appURL = setFromAttributesIfNotNull(appName, manifestContents, "App-URL");

                builtBy = setFromAttributesIfNotNull(builtBy, manifestContents, "Built-By");
                buildTimestamp = setFromAttributesIfNotNull(buildTimestamp, manifestContents, "Build-Timestamp");
                createdBy = setFromAttributesIfNotNull(createdBy, manifestContents, "Created-By");
                buildJdk = setFromAttributesIfNotNull(buildJdk, manifestContents, "Build-Jdk");
                buildOS = setFromAttributesIfNotNull(buildOS, manifestContents, "Build-OS");
            }
        } catch (IOException ex) {
            Logger.getLogger(AboutMenu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static String setFromAttributesIfNotNull(final String defaultValue, final Attributes manifestContents, final String attrName) {
        String result = manifestContents.getValue(attrName);
        
        if (result == null) {
            result = defaultValue;
        }
        
        return result;
    }
}
