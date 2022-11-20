/*
 *  Copyright (c) 2014ff Thomas Feuster
 *  All rights reserved.
 *  
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *  
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package tf.helper.general;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author thomas
 */
public class DependencyLoader {
    /**
     * Based on 
     * https://itnext.io/on-the-safe-side-log-your-3rd-party-package-versions-8b70ebdc9e1d
     * but in Jaca
     * 
     * @param excludePatterns List of name patterns that should be filtered out
     * @return Returns the list of Packages loaded filtered by excludePatterns
     */
    public static Set<String> getFilteredDependencies(final List<String> excludePatterns) {
        Set<String> result = new TreeSet<>();
        
        final Package[] packages = Package.getPackages();
        
        for (Package dependency : packages) {
            if (!startsWithAny(dependency.getName(), excludePatterns)) {
                if (dependency.getImplementationTitle() != null && 
                        dependency.getImplementationVersion() != null && 
                        dependency.getImplementationVendor() != null) {
                    result.add(String.format(
                            "%s v(%s) by %s", 
                            dependency.getImplementationTitle(), 
                            dependency.getImplementationVersion(), 
                            dependency.getImplementationVendor()));
                } else {
                    // we only want the first three parts of the name...
                    final String[] parts = dependency.getName().split("\\.");
                    String name = "";
                    for (int i = 0; i < Math.min(3, parts.length); i++) {
                        name += parts[i];
                        name += ".";
                    }
                    result.add(StringUtils.chop(name));
                }
            }
        }
        
        return result;
    }
    
    private static boolean startsWithAny(final String testString, final List<String>patterns) {
        boolean result = false;
        
        for (String pattern : patterns) {
            if (testString.startsWith(pattern)) {
                result = true;
                break;
            }
        }
        
        return result;
    }
}
