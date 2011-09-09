begin_unit
comment|'# Licensed to the Apache Software Foundation (ASF) under one or more'
nl|'\n'
comment|'# contributor license agreements.  See the NOTICE file distributed with'
nl|'\n'
comment|'# this work for additional information regarding copyright ownership.'
nl|'\n'
comment|'# The ASF licenses this file to You under the Apache License, Version 2.0'
nl|'\n'
comment|'# (the "License"); you may not use this file except in compliance with'
nl|'\n'
comment|'# the License.  You may obtain a copy of the License at'
nl|'\n'
comment|'#'
nl|'\n'
comment|'#     http://www.apache.org/licenses/LICENSE-2.0'
nl|'\n'
comment|'#'
nl|'\n'
comment|'# Unless required by applicable law or agreed to in writing, software'
nl|'\n'
comment|'# distributed under the License is distributed on an "AS IS" BASIS,'
nl|'\n'
comment|'# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.'
nl|'\n'
comment|'# See the License for the specific language governing permissions and'
nl|'\n'
comment|'# limitations under the License.'
nl|'\n'
nl|'\n'
name|'import'
name|'os'
newline|'\n'
name|'import'
name|'shutil'
newline|'\n'
name|'import'
name|'hashlib'
newline|'\n'
name|'import'
name|'httplib'
newline|'\n'
name|'import'
name|'re'
newline|'\n'
name|'import'
name|'urllib2'
newline|'\n'
name|'import'
name|'urlparse'
newline|'\n'
name|'import'
name|'sys'
newline|'\n'
name|'import'
name|'HTMLParser'
newline|'\n'
nl|'\n'
comment|'# This tool expects to find /lucene and /solr off the base URL.  You'
nl|'\n'
comment|'# must have a working gpg, tar, unzip in your path.  This has only'
nl|'\n'
comment|'# been tested on Linux so far!'
nl|'\n'
nl|'\n'
comment|'# http://s.apache.org/lusolr32rc2'
nl|'\n'
nl|'\n'
DECL|variable|JAVA5_HOME
name|'JAVA5_HOME'
op|'='
string|"'/usr/local/src/jdk1.5.0_22'"
newline|'\n'
DECL|variable|JAVA6_HOME
name|'JAVA6_HOME'
op|'='
string|"'/usr/local/src/jdk1.6.0_21'"
newline|'\n'
nl|'\n'
comment|'# TODO'
nl|'\n'
comment|'#   + verify KEYS contains key that signed the release'
nl|'\n'
comment|'#   + make sure changes HTML looks ok'
nl|'\n'
comment|'#   - verify license/notice of all dep jars'
nl|'\n'
comment|'#   - check maven'
nl|'\n'
comment|'#   - check JAR manifest version'
nl|'\n'
comment|'#   - check license/notice exist'
nl|'\n'
comment|'#   - check no "extra" files'
nl|'\n'
comment|'#   - make sure jars exist inside bin release'
nl|'\n'
comment|'#   - run "ant test"'
nl|'\n'
comment|'#   - make sure docs exist'
nl|'\n'
comment|'#   - use java5 for lucene/modules'
nl|'\n'
nl|'\n'
DECL|variable|reHREF
name|'reHREF'
op|'='
name|'re'
op|'.'
name|'compile'
op|'('
string|'\'<a href="(.*?)">(.*?)</a>\''
op|')'
newline|'\n'
nl|'\n'
comment|'# Set to True to avoid re-downloading the packages...'
nl|'\n'
DECL|variable|DEBUG
name|'DEBUG'
op|'='
name|'False'
newline|'\n'
nl|'\n'
DECL|function|getHREFs
name|'def'
name|'getHREFs'
op|'('
name|'urlString'
op|')'
op|':'
newline|'\n'
nl|'\n'
comment|'# Deref any redirects'
nl|'\n'
indent|'  '
name|'while'
name|'True'
op|':'
newline|'\n'
indent|'    '
name|'url'
op|'='
name|'urlparse'
op|'.'
name|'urlparse'
op|'('
name|'urlString'
op|')'
newline|'\n'
name|'h'
op|'='
name|'httplib'
op|'.'
name|'HTTPConnection'
op|'('
name|'url'
op|'.'
name|'netloc'
op|')'
newline|'\n'
name|'h'
op|'.'
name|'request'
op|'('
string|"'GET'"
op|','
name|'url'
op|'.'
name|'path'
op|')'
newline|'\n'
name|'r'
op|'='
name|'h'
op|'.'
name|'getresponse'
op|'('
op|')'
newline|'\n'
name|'newLoc'
op|'='
name|'r'
op|'.'
name|'getheader'
op|'('
string|"'location'"
op|')'
newline|'\n'
name|'if'
name|'newLoc'
name|'is'
name|'not'
name|'None'
op|':'
newline|'\n'
indent|'      '
name|'urlString'
op|'='
name|'newLoc'
newline|'\n'
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'      '
name|'break'
newline|'\n'
nl|'\n'
dedent|''
dedent|''
name|'links'
op|'='
op|'['
op|']'
newline|'\n'
name|'for'
name|'subUrl'
op|','
name|'text'
name|'in'
name|'reHREF'
op|'.'
name|'findall'
op|'('
name|'urllib2'
op|'.'
name|'urlopen'
op|'('
name|'urlString'
op|')'
op|'.'
name|'read'
op|'('
op|')'
op|')'
op|':'
newline|'\n'
indent|'    '
name|'fullURL'
op|'='
name|'urlparse'
op|'.'
name|'urljoin'
op|'('
name|'urlString'
op|','
name|'subUrl'
op|')'
newline|'\n'
name|'links'
op|'.'
name|'append'
op|'('
op|'('
name|'text'
op|','
name|'fullURL'
op|')'
op|')'
newline|'\n'
dedent|''
name|'return'
name|'links'
newline|'\n'
nl|'\n'
DECL|function|download
dedent|''
name|'def'
name|'download'
op|'('
name|'name'
op|','
name|'urlString'
op|','
name|'tmpDir'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'fileName'
op|'='
string|"'%s/%s'"
op|'%'
op|'('
name|'tmpDir'
op|','
name|'name'
op|')'
newline|'\n'
name|'if'
name|'DEBUG'
name|'and'
name|'os'
op|'.'
name|'path'
op|'.'
name|'exists'
op|'('
name|'fileName'
op|')'
op|':'
newline|'\n'
indent|'    '
name|'if'
name|'fileName'
op|'.'
name|'find'
op|'('
string|"'.asc'"
op|')'
op|'=='
op|'-'
number|'1'
op|':'
newline|'\n'
indent|'      '
name|'print'
string|"'    already done: %.1f MB'"
op|'%'
op|'('
name|'os'
op|'.'
name|'path'
op|'.'
name|'getsize'
op|'('
name|'fileName'
op|')'
op|'/'
number|'1024.'
op|'/'
number|'1024.'
op|')'
newline|'\n'
dedent|''
name|'return'
newline|'\n'
dedent|''
name|'fIn'
op|'='
name|'urllib2'
op|'.'
name|'urlopen'
op|'('
name|'urlString'
op|')'
newline|'\n'
name|'fOut'
op|'='
name|'open'
op|'('
name|'fileName'
op|','
string|"'wb'"
op|')'
newline|'\n'
name|'success'
op|'='
name|'False'
newline|'\n'
name|'try'
op|':'
newline|'\n'
indent|'    '
name|'while'
name|'True'
op|':'
newline|'\n'
indent|'      '
name|'s'
op|'='
name|'fIn'
op|'.'
name|'read'
op|'('
number|'65536'
op|')'
newline|'\n'
name|'if'
name|'s'
op|'=='
string|"''"
op|':'
newline|'\n'
indent|'        '
name|'break'
newline|'\n'
dedent|''
name|'fOut'
op|'.'
name|'write'
op|'('
name|'s'
op|')'
newline|'\n'
dedent|''
name|'fOut'
op|'.'
name|'close'
op|'('
op|')'
newline|'\n'
name|'fIn'
op|'.'
name|'close'
op|'('
op|')'
newline|'\n'
name|'success'
op|'='
name|'True'
newline|'\n'
dedent|''
name|'finally'
op|':'
newline|'\n'
indent|'    '
name|'fIn'
op|'.'
name|'close'
op|'('
op|')'
newline|'\n'
name|'fOut'
op|'.'
name|'close'
op|'('
op|')'
newline|'\n'
name|'if'
name|'not'
name|'success'
op|':'
newline|'\n'
indent|'      '
name|'os'
op|'.'
name|'remove'
op|'('
name|'fileName'
op|')'
newline|'\n'
dedent|''
dedent|''
name|'if'
name|'fileName'
op|'.'
name|'find'
op|'('
string|"'.asc'"
op|')'
op|'=='
op|'-'
number|'1'
op|':'
newline|'\n'
indent|'    '
name|'print'
string|"'    %.1f MB'"
op|'%'
op|'('
name|'os'
op|'.'
name|'path'
op|'.'
name|'getsize'
op|'('
name|'fileName'
op|')'
op|'/'
number|'1024.'
op|'/'
number|'1024.'
op|')'
newline|'\n'
nl|'\n'
DECL|function|load
dedent|''
dedent|''
name|'def'
name|'load'
op|'('
name|'urlString'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'return'
name|'urllib2'
op|'.'
name|'urlopen'
op|'('
name|'urlString'
op|')'
op|'.'
name|'read'
op|'('
op|')'
newline|'\n'
nl|'\n'
DECL|function|checkSigs
dedent|''
name|'def'
name|'checkSigs'
op|'('
name|'project'
op|','
name|'urlString'
op|','
name|'version'
op|','
name|'tmpDir'
op|')'
op|':'
newline|'\n'
nl|'\n'
indent|'  '
name|'print'
string|"'  test basics...'"
newline|'\n'
name|'ents'
op|'='
name|'getDirEntries'
op|'('
name|'urlString'
op|')'
newline|'\n'
name|'artifact'
op|'='
name|'None'
newline|'\n'
name|'keysURL'
op|'='
name|'None'
newline|'\n'
name|'changesURL'
op|'='
name|'None'
newline|'\n'
name|'mavenURL'
op|'='
name|'None'
newline|'\n'
name|'expectedSigs'
op|'='
op|'['
string|"'asc'"
op|','
string|"'md5'"
op|','
string|"'sha1'"
op|']'
newline|'\n'
name|'artifacts'
op|'='
op|'['
op|']'
newline|'\n'
name|'for'
name|'text'
op|','
name|'subURL'
name|'in'
name|'ents'
op|':'
newline|'\n'
indent|'    '
name|'if'
name|'text'
op|'=='
string|"'KEYS'"
op|':'
newline|'\n'
indent|'      '
name|'keysURL'
op|'='
name|'subURL'
newline|'\n'
dedent|''
name|'elif'
name|'text'
op|'=='
string|"'maven/'"
op|':'
newline|'\n'
indent|'      '
name|'mavenURL'
op|'='
name|'subURL'
newline|'\n'
dedent|''
name|'elif'
name|'text'
op|'.'
name|'startswith'
op|'('
string|"'changes'"
op|')'
op|':'
newline|'\n'
indent|'      '
name|'if'
name|'text'
name|'not'
name|'in'
op|'('
string|"'changes/'"
op|','
string|"'changes-%s/'"
op|'%'
name|'version'
op|')'
op|':'
newline|'\n'
indent|'        '
name|'raise'
name|'RuntimeError'
op|'('
string|"'%s: found %s vs expected changes-%s/'"
op|'%'
op|'('
name|'project'
op|','
name|'text'
op|','
name|'version'
op|')'
op|')'
newline|'\n'
dedent|''
name|'changesURL'
op|'='
name|'subURL'
newline|'\n'
dedent|''
name|'elif'
name|'artifact'
op|'=='
name|'None'
op|':'
newline|'\n'
indent|'      '
name|'artifact'
op|'='
name|'text'
newline|'\n'
name|'artifactURL'
op|'='
name|'subURL'
newline|'\n'
name|'if'
name|'project'
op|'=='
string|"'solr'"
op|':'
newline|'\n'
indent|'        '
name|'expected'
op|'='
string|"'apache-solr-%s'"
op|'%'
name|'version'
newline|'\n'
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'        '
name|'expected'
op|'='
string|"'lucene-%s'"
op|'%'
name|'version'
newline|'\n'
dedent|''
name|'if'
name|'not'
name|'artifact'
op|'.'
name|'startswith'
op|'('
name|'expected'
op|')'
op|':'
newline|'\n'
indent|'        '
name|'raise'
name|'RuntimeError'
op|'('
string|"'%s: unknown artifact %s: expected prefix %s'"
op|'%'
op|'('
name|'project'
op|','
name|'text'
op|','
name|'expected'
op|')'
op|')'
newline|'\n'
dedent|''
name|'sigs'
op|'='
op|'['
op|']'
newline|'\n'
dedent|''
name|'elif'
name|'text'
op|'.'
name|'startswith'
op|'('
name|'artifact'
op|'+'
string|"'.'"
op|')'
op|':'
newline|'\n'
indent|'      '
name|'sigs'
op|'.'
name|'append'
op|'('
name|'text'
op|'['
name|'len'
op|'('
name|'artifact'
op|')'
op|'+'
number|'1'
op|':'
op|']'
op|')'
newline|'\n'
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'      '
name|'if'
name|'sigs'
op|'!='
name|'expectedSigs'
op|':'
newline|'\n'
indent|'        '
name|'raise'
name|'RuntimeError'
op|'('
string|"'%s: artifact %s has wrong sigs: expected %s but got %s'"
op|'%'
op|'('
name|'project'
op|','
name|'artifact'
op|','
name|'expectedSigs'
op|','
name|'sigs'
op|')'
op|')'
newline|'\n'
dedent|''
name|'artifacts'
op|'.'
name|'append'
op|'('
op|'('
name|'artifact'
op|','
name|'artifactURL'
op|')'
op|')'
newline|'\n'
name|'artifact'
op|'='
name|'text'
newline|'\n'
name|'artifactURL'
op|'='
name|'subURL'
newline|'\n'
name|'sigs'
op|'='
op|'['
op|']'
newline|'\n'
nl|'\n'
dedent|''
dedent|''
name|'if'
name|'sigs'
op|'!='
op|'['
op|']'
op|':'
newline|'\n'
indent|'    '
name|'artifacts'
op|'.'
name|'append'
op|'('
op|'('
name|'artifact'
op|','
name|'artifactURL'
op|')'
op|')'
newline|'\n'
name|'if'
name|'sigs'
op|'!='
name|'expectedSigs'
op|':'
newline|'\n'
indent|'      '
name|'raise'
name|'RuntimeError'
op|'('
string|"'%s: artifact %s has wrong sigs: expected %s but got %s'"
op|'%'
op|'('
name|'project'
op|','
name|'artifact'
op|','
name|'expectedSigs'
op|','
name|'sigs'
op|')'
op|')'
newline|'\n'
nl|'\n'
dedent|''
dedent|''
name|'if'
name|'project'
op|'=='
string|"'lucene'"
op|':'
newline|'\n'
indent|'    '
name|'expected'
op|'='
op|'['
string|"'lucene-%s-src.tgz'"
op|'%'
name|'version'
op|','
nl|'\n'
string|"'lucene-%s.tgz'"
op|'%'
name|'version'
op|','
nl|'\n'
string|"'lucene-%s.zip'"
op|'%'
name|'version'
op|']'
newline|'\n'
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'    '
name|'expected'
op|'='
op|'['
string|"'apache-solr-%s-src.tgz'"
op|'%'
name|'version'
op|','
nl|'\n'
string|"'apache-solr-%s.tgz'"
op|'%'
name|'version'
op|','
nl|'\n'
string|"'apache-solr-%s.zip'"
op|'%'
name|'version'
op|']'
newline|'\n'
nl|'\n'
dedent|''
name|'actual'
op|'='
op|'['
name|'x'
op|'['
number|'0'
op|']'
name|'for'
name|'x'
name|'in'
name|'artifacts'
op|']'
newline|'\n'
name|'if'
name|'expected'
op|'!='
name|'actual'
op|':'
newline|'\n'
indent|'    '
name|'raise'
name|'RuntimeError'
op|'('
string|"'%s: wrong artifacts: expected %s but got %s'"
op|'%'
op|'('
name|'project'
op|','
name|'expected'
op|','
name|'actual'
op|')'
op|')'
newline|'\n'
nl|'\n'
dedent|''
name|'if'
name|'keysURL'
name|'is'
name|'None'
op|':'
newline|'\n'
indent|'    '
name|'raise'
name|'RuntimeError'
op|'('
string|"'%s is missing KEYS'"
op|'%'
name|'project'
op|')'
newline|'\n'
nl|'\n'
dedent|''
name|'download'
op|'('
string|"'%s.KEYS'"
op|'%'
name|'project'
op|','
name|'keysURL'
op|','
name|'tmpDir'
op|')'
newline|'\n'
nl|'\n'
name|'keysFile'
op|'='
string|"'%s/%s.KEYS'"
op|'%'
op|'('
name|'tmpDir'
op|','
name|'project'
op|')'
newline|'\n'
nl|'\n'
comment|'# Set up clean gpg world; import keys file:'
nl|'\n'
name|'gpgHomeDir'
op|'='
string|"'%s/%s.gpg'"
op|'%'
op|'('
name|'tmpDir'
op|','
name|'project'
op|')'
newline|'\n'
name|'if'
name|'os'
op|'.'
name|'path'
op|'.'
name|'exists'
op|'('
name|'gpgHomeDir'
op|')'
op|':'
newline|'\n'
indent|'    '
name|'shutil'
op|'.'
name|'rmtree'
op|'('
name|'gpgHomeDir'
op|')'
newline|'\n'
dedent|''
name|'os'
op|'.'
name|'makedirs'
op|'('
name|'gpgHomeDir'
op|','
number|'0700'
op|')'
newline|'\n'
name|'run'
op|'('
string|"'gpg --homedir %s --import %s'"
op|'%'
op|'('
name|'gpgHomeDir'
op|','
name|'keysFile'
op|')'
op|','
nl|'\n'
string|"'%s/%s.gpg.import.log 2>&1'"
op|'%'
op|'('
name|'tmpDir'
op|','
name|'project'
op|')'
op|')'
newline|'\n'
nl|'\n'
name|'if'
name|'mavenURL'
name|'is'
name|'None'
op|':'
newline|'\n'
indent|'    '
name|'raise'
name|'RuntimeError'
op|'('
string|"'%s is missing maven'"
op|'%'
name|'project'
op|')'
newline|'\n'
nl|'\n'
dedent|''
name|'if'
name|'project'
op|'=='
string|"'lucene'"
op|':'
newline|'\n'
indent|'    '
name|'if'
name|'changesURL'
name|'is'
name|'None'
op|':'
newline|'\n'
indent|'      '
name|'raise'
name|'RuntimeError'
op|'('
string|"'%s is missing changes-%s'"
op|'%'
op|'('
name|'project'
op|','
name|'version'
op|')'
op|')'
newline|'\n'
dedent|''
name|'testChanges'
op|'('
name|'project'
op|','
name|'version'
op|','
name|'changesURL'
op|')'
newline|'\n'
nl|'\n'
dedent|''
name|'for'
name|'artifact'
op|','
name|'urlString'
name|'in'
name|'artifacts'
op|':'
newline|'\n'
indent|'    '
name|'print'
string|"'  download %s...'"
op|'%'
name|'artifact'
newline|'\n'
name|'download'
op|'('
name|'artifact'
op|','
name|'urlString'
op|','
name|'tmpDir'
op|')'
newline|'\n'
name|'verifyDigests'
op|'('
name|'artifact'
op|','
name|'urlString'
op|','
name|'tmpDir'
op|')'
newline|'\n'
nl|'\n'
name|'print'
string|"'    verify sig'"
newline|'\n'
comment|'# Test sig'
nl|'\n'
name|'download'
op|'('
name|'artifact'
op|'+'
string|"'.asc'"
op|','
name|'urlString'
op|'+'
string|"'.asc'"
op|','
name|'tmpDir'
op|')'
newline|'\n'
name|'sigFile'
op|'='
string|"'%s/%s.asc'"
op|'%'
op|'('
name|'tmpDir'
op|','
name|'artifact'
op|')'
newline|'\n'
name|'artifactFile'
op|'='
string|"'%s/%s'"
op|'%'
op|'('
name|'tmpDir'
op|','
name|'artifact'
op|')'
newline|'\n'
name|'logFile'
op|'='
string|"'%s/%s.%s.gpg.verify.log'"
op|'%'
op|'('
name|'tmpDir'
op|','
name|'project'
op|','
name|'artifact'
op|')'
newline|'\n'
name|'run'
op|'('
string|"'gpg --homedir %s --verify %s %s'"
op|'%'
op|'('
name|'gpgHomeDir'
op|','
name|'sigFile'
op|','
name|'artifactFile'
op|')'
op|','
nl|'\n'
name|'logFile'
op|')'
newline|'\n'
comment|'# Forward any GPG warnings:'
nl|'\n'
name|'f'
op|'='
name|'open'
op|'('
name|'logFile'
op|','
string|"'rb'"
op|')'
newline|'\n'
name|'for'
name|'line'
name|'in'
name|'f'
op|'.'
name|'readlines'
op|'('
op|')'
op|':'
newline|'\n'
indent|'      '
name|'if'
name|'line'
op|'.'
name|'lower'
op|'('
op|')'
op|'.'
name|'find'
op|'('
string|"'warning'"
op|')'
op|'!='
op|'-'
number|'1'
op|':'
newline|'\n'
indent|'        '
name|'print'
string|"'      GPG: %s'"
op|'%'
name|'line'
op|'.'
name|'strip'
op|'('
op|')'
newline|'\n'
dedent|''
dedent|''
name|'f'
op|'.'
name|'close'
op|'('
op|')'
newline|'\n'
nl|'\n'
DECL|function|testChanges
dedent|''
dedent|''
name|'def'
name|'testChanges'
op|'('
name|'project'
op|','
name|'version'
op|','
name|'changesURLString'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'print'
string|"'  check changes HTML...'"
newline|'\n'
name|'changesURL'
op|'='
name|'None'
newline|'\n'
name|'contribChangesURL'
op|'='
name|'None'
newline|'\n'
name|'for'
name|'text'
op|','
name|'subURL'
name|'in'
name|'getDirEntries'
op|'('
name|'changesURLString'
op|')'
op|':'
newline|'\n'
indent|'    '
name|'if'
name|'text'
op|'=='
string|"'Changes.html'"
op|':'
newline|'\n'
indent|'      '
name|'changesURL'
op|'='
name|'subURL'
newline|'\n'
dedent|''
name|'elif'
name|'text'
op|'=='
string|"'Contrib-Changes.html'"
op|':'
newline|'\n'
indent|'      '
name|'contribChangesURL'
op|'='
name|'subURL'
newline|'\n'
nl|'\n'
dedent|''
dedent|''
name|'if'
name|'changesURL'
name|'is'
name|'None'
op|':'
newline|'\n'
indent|'    '
name|'raise'
name|'RuntimeError'
op|'('
string|"'did not see Changes.html link from %s'"
op|'%'
name|'changesURLString'
op|')'
newline|'\n'
dedent|''
name|'if'
name|'contribChangesURL'
name|'is'
name|'None'
op|':'
newline|'\n'
indent|'    '
name|'raise'
name|'RuntimeError'
op|'('
string|"'did not see Contrib-Changes.html link from %s'"
op|'%'
name|'changesURLString'
op|')'
newline|'\n'
nl|'\n'
dedent|''
name|'s'
op|'='
name|'load'
op|'('
name|'changesURL'
op|')'
newline|'\n'
name|'checkChangesContent'
op|'('
name|'s'
op|','
name|'version'
op|','
name|'changesURL'
op|','
name|'project'
op|','
name|'True'
op|')'
newline|'\n'
nl|'\n'
DECL|function|testChangesText
dedent|''
name|'def'
name|'testChangesText'
op|'('
name|'dir'
op|','
name|'version'
op|','
name|'project'
op|')'
op|':'
newline|'\n'
indent|'  '
string|'"Checks all CHANGES.txt under this dir."'
newline|'\n'
name|'for'
name|'root'
op|','
name|'dirs'
op|','
name|'files'
name|'in'
name|'os'
op|'.'
name|'walk'
op|'('
name|'dir'
op|')'
op|':'
newline|'\n'
nl|'\n'
comment|'# NOTE: O(N) but N should be smallish:'
nl|'\n'
indent|'    '
name|'if'
string|"'CHANGES.txt'"
name|'in'
name|'files'
op|':'
newline|'\n'
indent|'      '
name|'fullPath'
op|'='
string|"'%s/CHANGES.txt'"
op|'%'
name|'root'
newline|'\n'
comment|"#print 'CHECK %s' % fullPath"
nl|'\n'
name|'checkChangesContent'
op|'('
name|'open'
op|'('
name|'fullPath'
op|')'
op|'.'
name|'read'
op|'('
op|')'
op|','
name|'version'
op|','
name|'fullPath'
op|','
name|'project'
op|','
name|'False'
op|')'
newline|'\n'
nl|'\n'
DECL|function|checkChangesContent
dedent|''
dedent|''
dedent|''
name|'def'
name|'checkChangesContent'
op|'('
name|'s'
op|','
name|'version'
op|','
name|'name'
op|','
name|'project'
op|','
name|'isHTML'
op|')'
op|':'
newline|'\n'
nl|'\n'
indent|'  '
name|'if'
name|'isHTML'
name|'and'
name|'s'
op|'.'
name|'find'
op|'('
string|"'Release %s'"
op|'%'
name|'version'
op|')'
op|'=='
op|'-'
number|'1'
op|':'
newline|'\n'
indent|'    '
name|'raise'
name|'RuntimeError'
op|'('
string|'\'did not see "Release %s" in %s\''
op|'%'
op|'('
name|'version'
op|','
name|'name'
op|')'
op|')'
newline|'\n'
nl|'\n'
dedent|''
name|'if'
name|'s'
op|'.'
name|'lower'
op|'('
op|')'
op|'.'
name|'find'
op|'('
string|"'not yet released'"
op|')'
op|'!='
op|'-'
number|'1'
op|':'
newline|'\n'
indent|'    '
name|'raise'
name|'RuntimeError'
op|'('
string|'\'saw "not yet released" in %s\''
op|'%'
name|'name'
op|')'
newline|'\n'
nl|'\n'
dedent|''
name|'if'
name|'not'
name|'isHTML'
op|':'
newline|'\n'
indent|'    '
name|'if'
name|'project'
op|'=='
string|"'lucene'"
op|':'
newline|'\n'
indent|'      '
name|'sub'
op|'='
string|"'Lucene %s'"
op|'%'
name|'version'
newline|'\n'
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'      '
name|'sub'
op|'='
name|'version'
newline|'\n'
nl|'\n'
dedent|''
name|'if'
name|'s'
op|'.'
name|'find'
op|'('
name|'sub'
op|')'
op|'=='
op|'-'
number|'1'
op|':'
newline|'\n'
comment|'# contrib/benchmark never seems to include release info:'
nl|'\n'
indent|'      '
name|'if'
name|'name'
op|'.'
name|'find'
op|'('
string|"'/benchmark/'"
op|')'
op|'=='
op|'-'
number|'1'
op|':'
newline|'\n'
indent|'        '
name|'raise'
name|'RuntimeError'
op|'('
string|'\'did not see "%s" in %s\''
op|'%'
op|'('
name|'sub'
op|','
name|'name'
op|')'
op|')'
newline|'\n'
nl|'\n'
DECL|function|run
dedent|''
dedent|''
dedent|''
dedent|''
name|'def'
name|'run'
op|'('
name|'command'
op|','
name|'logFile'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'if'
name|'os'
op|'.'
name|'system'
op|'('
string|"'%s > %s 2>&1'"
op|'%'
op|'('
name|'command'
op|','
name|'logFile'
op|')'
op|')'
op|':'
newline|'\n'
indent|'    '
name|'raise'
name|'RuntimeError'
op|'('
string|'\'command "%s" failed; see log file %s/%s\''
op|'%'
op|'('
name|'command'
op|','
name|'os'
op|'.'
name|'getcwd'
op|'('
op|')'
op|','
name|'logFile'
op|')'
op|')'
newline|'\n'
nl|'\n'
DECL|function|verifyDigests
dedent|''
dedent|''
name|'def'
name|'verifyDigests'
op|'('
name|'artifact'
op|','
name|'urlString'
op|','
name|'tmpDir'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'print'
string|"'    verify md5/sha1 digests'"
newline|'\n'
name|'md5Expected'
op|','
name|'t'
op|'='
name|'load'
op|'('
name|'urlString'
op|'+'
string|"'.md5'"
op|')'
op|'.'
name|'strip'
op|'('
op|')'
op|'.'
name|'split'
op|'('
op|')'
newline|'\n'
name|'if'
name|'t'
op|'!='
string|"'*'"
op|'+'
name|'artifact'
op|':'
newline|'\n'
indent|'    '
name|'raise'
name|'RuntimeError'
op|'('
string|"'MD5 %s.md5 lists artifact %s but expected *%s'"
op|'%'
op|'('
name|'urlString'
op|','
name|'t'
op|','
name|'artifact'
op|')'
op|')'
newline|'\n'
nl|'\n'
dedent|''
name|'sha1Expected'
op|','
name|'t'
op|'='
name|'load'
op|'('
name|'urlString'
op|'+'
string|"'.sha1'"
op|')'
op|'.'
name|'strip'
op|'('
op|')'
op|'.'
name|'split'
op|'('
op|')'
newline|'\n'
name|'if'
name|'t'
op|'!='
string|"'*'"
op|'+'
name|'artifact'
op|':'
newline|'\n'
indent|'    '
name|'raise'
name|'RuntimeError'
op|'('
string|"'SHA1 %s.sha1 lists artifact %s but expected *%s'"
op|'%'
op|'('
name|'urlString'
op|','
name|'t'
op|','
name|'artifact'
op|')'
op|')'
newline|'\n'
nl|'\n'
dedent|''
name|'m'
op|'='
name|'hashlib'
op|'.'
name|'md5'
op|'('
op|')'
newline|'\n'
name|'s'
op|'='
name|'hashlib'
op|'.'
name|'sha1'
op|'('
op|')'
newline|'\n'
name|'f'
op|'='
name|'open'
op|'('
string|"'%s/%s'"
op|'%'
op|'('
name|'tmpDir'
op|','
name|'artifact'
op|')'
op|')'
newline|'\n'
name|'while'
name|'True'
op|':'
newline|'\n'
indent|'    '
name|'x'
op|'='
name|'f'
op|'.'
name|'read'
op|'('
number|'65536'
op|')'
newline|'\n'
name|'if'
name|'x'
op|'=='
string|"''"
op|':'
newline|'\n'
indent|'      '
name|'break'
newline|'\n'
dedent|''
name|'m'
op|'.'
name|'update'
op|'('
name|'x'
op|')'
newline|'\n'
name|'s'
op|'.'
name|'update'
op|'('
name|'x'
op|')'
newline|'\n'
dedent|''
name|'f'
op|'.'
name|'close'
op|'('
op|')'
newline|'\n'
name|'md5Actual'
op|'='
name|'m'
op|'.'
name|'hexdigest'
op|'('
op|')'
newline|'\n'
name|'sha1Actual'
op|'='
name|'s'
op|'.'
name|'hexdigest'
op|'('
op|')'
newline|'\n'
name|'if'
name|'md5Actual'
op|'!='
name|'md5Expected'
op|':'
newline|'\n'
indent|'    '
name|'raise'
name|'RuntimeError'
op|'('
string|"'MD5 digest mismatch for %s: expected %s but got %s'"
op|'%'
op|'('
name|'artifact'
op|','
name|'md5Expected'
op|','
name|'md5Actual'
op|')'
op|')'
newline|'\n'
dedent|''
name|'if'
name|'sha1Actual'
op|'!='
name|'sha1Expected'
op|':'
newline|'\n'
indent|'    '
name|'raise'
name|'RuntimeError'
op|'('
string|"'SHA1 digest mismatch for %s: expected %s but got %s'"
op|'%'
op|'('
name|'artifact'
op|','
name|'sha1Expected'
op|','
name|'sha1Actual'
op|')'
op|')'
newline|'\n'
nl|'\n'
DECL|function|getDirEntries
dedent|''
dedent|''
name|'def'
name|'getDirEntries'
op|'('
name|'urlString'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'links'
op|'='
name|'getHREFs'
op|'('
name|'urlString'
op|')'
newline|'\n'
name|'for'
name|'i'
op|','
op|'('
name|'text'
op|','
name|'subURL'
op|')'
name|'in'
name|'enumerate'
op|'('
name|'links'
op|')'
op|':'
newline|'\n'
indent|'    '
name|'if'
name|'text'
op|'=='
string|"'Parent Directory'"
op|':'
newline|'\n'
indent|'      '
name|'return'
name|'links'
op|'['
op|'('
name|'i'
op|'+'
number|'1'
op|')'
op|':'
op|']'
newline|'\n'
nl|'\n'
DECL|function|unpack
dedent|''
dedent|''
dedent|''
name|'def'
name|'unpack'
op|'('
name|'project'
op|','
name|'tmpDir'
op|','
name|'artifact'
op|','
name|'version'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'destDir'
op|'='
string|"'%s/unpack'"
op|'%'
name|'tmpDir'
newline|'\n'
name|'if'
name|'os'
op|'.'
name|'path'
op|'.'
name|'exists'
op|'('
name|'destDir'
op|')'
op|':'
newline|'\n'
indent|'    '
name|'shutil'
op|'.'
name|'rmtree'
op|'('
name|'destDir'
op|')'
newline|'\n'
dedent|''
name|'os'
op|'.'
name|'makedirs'
op|'('
name|'destDir'
op|')'
newline|'\n'
name|'os'
op|'.'
name|'chdir'
op|'('
name|'destDir'
op|')'
newline|'\n'
name|'print'
string|"'    unpack %s...'"
op|'%'
name|'artifact'
newline|'\n'
name|'unpackLogFile'
op|'='
string|"'%s/%s-unpack-%s.log'"
op|'%'
op|'('
name|'tmpDir'
op|','
name|'project'
op|','
name|'artifact'
op|')'
newline|'\n'
name|'if'
name|'artifact'
op|'.'
name|'endswith'
op|'('
string|"'.tar.gz'"
op|')'
name|'or'
name|'artifact'
op|'.'
name|'endswith'
op|'('
string|"'.tgz'"
op|')'
op|':'
newline|'\n'
indent|'    '
name|'run'
op|'('
string|"'tar xzf %s/%s'"
op|'%'
op|'('
name|'tmpDir'
op|','
name|'artifact'
op|')'
op|','
name|'unpackLogFile'
op|')'
newline|'\n'
dedent|''
name|'elif'
name|'artifact'
op|'.'
name|'endswith'
op|'('
string|"'.zip'"
op|')'
op|':'
newline|'\n'
indent|'    '
name|'run'
op|'('
string|"'unzip %s/%s'"
op|'%'
op|'('
name|'tmpDir'
op|','
name|'artifact'
op|')'
op|','
name|'unpackLogFile'
op|')'
newline|'\n'
nl|'\n'
comment|'# make sure it unpacks to proper subdir'
nl|'\n'
dedent|''
name|'l'
op|'='
name|'os'
op|'.'
name|'listdir'
op|'('
name|'destDir'
op|')'
newline|'\n'
name|'if'
name|'project'
op|'=='
string|"'solr'"
op|':'
newline|'\n'
indent|'    '
name|'expected'
op|'='
string|"'apache-%s-%s'"
op|'%'
op|'('
name|'project'
op|','
name|'version'
op|')'
newline|'\n'
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'    '
name|'expected'
op|'='
string|"'%s-%s'"
op|'%'
op|'('
name|'project'
op|','
name|'version'
op|')'
newline|'\n'
dedent|''
name|'if'
name|'l'
op|'!='
op|'['
name|'expected'
op|']'
op|':'
newline|'\n'
indent|'    '
name|'raise'
name|'RuntimeError'
op|'('
string|"'unpack produced entries %s; expected only %s'"
op|'%'
op|'('
name|'l'
op|','
name|'expected'
op|')'
op|')'
newline|'\n'
nl|'\n'
dedent|''
name|'unpackPath'
op|'='
string|"'%s/%s'"
op|'%'
op|'('
name|'destDir'
op|','
name|'expected'
op|')'
newline|'\n'
name|'verifyUnpacked'
op|'('
name|'project'
op|','
name|'artifact'
op|','
name|'unpackPath'
op|','
name|'version'
op|')'
newline|'\n'
nl|'\n'
DECL|function|verifyUnpacked
dedent|''
name|'def'
name|'verifyUnpacked'
op|'('
name|'project'
op|','
name|'artifact'
op|','
name|'unpackPath'
op|','
name|'version'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'os'
op|'.'
name|'chdir'
op|'('
name|'unpackPath'
op|')'
newline|'\n'
name|'isSrc'
op|'='
name|'artifact'
op|'.'
name|'find'
op|'('
string|"'-src'"
op|')'
op|'!='
op|'-'
number|'1'
newline|'\n'
name|'l'
op|'='
name|'os'
op|'.'
name|'listdir'
op|'('
name|'unpackPath'
op|')'
newline|'\n'
name|'textFiles'
op|'='
op|'['
string|"'LICENSE'"
op|','
string|"'NOTICE'"
op|','
string|"'README'"
op|']'
newline|'\n'
name|'if'
name|'project'
op|'=='
string|"'lucene'"
op|':'
newline|'\n'
indent|'    '
name|'textFiles'
op|'.'
name|'extend'
op|'('
op|'('
string|"'JRE_VERSION_MIGRATION'"
op|','
string|"'CHANGES'"
op|')'
op|')'
newline|'\n'
name|'if'
name|'isSrc'
op|':'
newline|'\n'
indent|'      '
name|'textFiles'
op|'.'
name|'append'
op|'('
string|"'BUILD'"
op|')'
newline|'\n'
dedent|''
dedent|''
name|'for'
name|'fileName'
name|'in'
name|'textFiles'
op|':'
newline|'\n'
indent|'    '
name|'fileName'
op|'+='
string|"'.txt'"
newline|'\n'
name|'if'
name|'fileName'
name|'not'
name|'in'
name|'l'
op|':'
newline|'\n'
indent|'      '
name|'raise'
name|'RuntimeError'
op|'('
string|'\'file "%s" is missing from artifact %s\''
op|'%'
op|'('
name|'fileName'
op|','
name|'artifact'
op|')'
op|')'
newline|'\n'
dedent|''
name|'l'
op|'.'
name|'remove'
op|'('
name|'fileName'
op|')'
newline|'\n'
nl|'\n'
dedent|''
name|'if'
name|'not'
name|'isSrc'
op|':'
newline|'\n'
indent|'    '
name|'if'
name|'project'
op|'=='
string|"'lucene'"
op|':'
newline|'\n'
indent|'      '
name|'expectedJARs'
op|'='
op|'('
string|"'lucene-core-%s'"
op|'%'
name|'version'
op|','
nl|'\n'
string|"'lucene-core-%s-javadoc'"
op|'%'
name|'version'
op|','
nl|'\n'
string|"'lucene-test-framework-%s'"
op|'%'
name|'version'
op|','
nl|'\n'
string|"'lucene-test-framework-%s-javadoc'"
op|'%'
name|'version'
op|')'
newline|'\n'
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'      '
name|'expectedJARs'
op|'='
op|'('
op|')'
newline|'\n'
nl|'\n'
dedent|''
name|'for'
name|'fileName'
name|'in'
name|'expectedJARs'
op|':'
newline|'\n'
indent|'      '
name|'fileName'
op|'+='
string|"'.jar'"
newline|'\n'
name|'if'
name|'fileName'
name|'not'
name|'in'
name|'l'
op|':'
newline|'\n'
indent|'        '
name|'raise'
name|'RuntimeError'
op|'('
string|'\'%s: file "%s" is missing from artifact %s\''
op|'%'
op|'('
name|'project'
op|','
name|'fileName'
op|','
name|'artifact'
op|')'
op|')'
newline|'\n'
dedent|''
name|'l'
op|'.'
name|'remove'
op|'('
name|'fileName'
op|')'
newline|'\n'
nl|'\n'
dedent|''
dedent|''
name|'if'
name|'project'
op|'=='
string|"'lucene'"
op|':'
newline|'\n'
indent|'    '
name|'extras'
op|'='
op|'('
string|"'lib'"
op|','
string|"'docs'"
op|','
string|"'contrib'"
op|')'
newline|'\n'
name|'if'
name|'isSrc'
op|':'
newline|'\n'
indent|'      '
name|'extras'
op|'+='
op|'('
string|"'build.xml'"
op|','
string|"'index.html'"
op|','
string|"'common-build.xml'"
op|','
string|"'src'"
op|','
string|"'backwards'"
op|')'
newline|'\n'
dedent|''
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'    '
name|'extras'
op|'='
op|'('
op|')'
newline|'\n'
nl|'\n'
dedent|''
name|'for'
name|'e'
name|'in'
name|'extras'
op|':'
newline|'\n'
indent|'    '
name|'if'
name|'e'
name|'not'
name|'in'
name|'l'
op|':'
newline|'\n'
indent|'      '
name|'raise'
name|'RuntimeError'
op|'('
string|"'%s: %s missing from artifact %s'"
op|'%'
op|'('
name|'project'
op|','
name|'e'
op|','
name|'artifact'
op|')'
op|')'
newline|'\n'
dedent|''
name|'l'
op|'.'
name|'remove'
op|'('
name|'e'
op|')'
newline|'\n'
nl|'\n'
dedent|''
name|'if'
name|'project'
op|'=='
string|"'lucene'"
op|':'
newline|'\n'
indent|'    '
name|'if'
name|'len'
op|'('
name|'l'
op|')'
op|'>'
number|'0'
op|':'
newline|'\n'
indent|'      '
name|'raise'
name|'RuntimeError'
op|'('
string|"'%s: unexpected files/dirs in artifact %s: %s'"
op|'%'
op|'('
name|'project'
op|','
name|'artifact'
op|','
name|'l'
op|')'
op|')'
newline|'\n'
nl|'\n'
dedent|''
dedent|''
name|'if'
name|'isSrc'
op|':'
newline|'\n'
indent|'    '
name|'if'
name|'project'
op|'=='
string|"'lucene'"
op|':'
newline|'\n'
indent|'      '
name|'print'
string|"'    run tests w/ Java 5...'"
newline|'\n'
name|'run'
op|'('
string|"'export JAVA_HOME=%s; ant test'"
op|'%'
name|'JAVA5_HOME'
op|','
string|"'%s/test.log'"
op|'%'
name|'unpackPath'
op|')'
newline|'\n'
name|'run'
op|'('
string|"'export JAVA_HOME=%s; ant jar'"
op|'%'
name|'JAVA5_HOME'
op|','
string|"'%s/compile.log'"
op|'%'
name|'unpackPath'
op|')'
newline|'\n'
name|'testDemo'
op|'('
name|'isSrc'
op|','
name|'version'
op|')'
newline|'\n'
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'      '
name|'print'
string|"'    run tests w/ Java 6...'"
newline|'\n'
name|'run'
op|'('
string|"'export JAVA_HOME=%s; ant test'"
op|'%'
name|'JAVA6_HOME'
op|','
string|"'%s/test.log'"
op|'%'
name|'unpackPath'
op|')'
newline|'\n'
dedent|''
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'    '
name|'if'
name|'project'
op|'=='
string|"'lucene'"
op|':'
newline|'\n'
indent|'      '
name|'testDemo'
op|'('
name|'isSrc'
op|','
name|'version'
op|')'
newline|'\n'
nl|'\n'
dedent|''
dedent|''
name|'testChangesText'
op|'('
string|"'.'"
op|','
name|'version'
op|','
name|'project'
op|')'
newline|'\n'
nl|'\n'
DECL|function|testDemo
dedent|''
name|'def'
name|'testDemo'
op|'('
name|'isSrc'
op|','
name|'version'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'print'
string|"'    test demo...'"
newline|'\n'
name|'if'
name|'isSrc'
op|':'
newline|'\n'
comment|'# allow lucene dev version to be either 3.3 or 3.3.0:'
nl|'\n'
indent|'    '
name|'if'
name|'version'
op|'.'
name|'endswith'
op|'('
string|"'.0'"
op|')'
op|':'
newline|'\n'
indent|'      '
name|'cp'
op|'='
string|"'build/lucene-core-%s-SNAPSHOT.jar:build/contrib/demo/lucene-demo-%s-SNAPSHOT.jar'"
op|'%'
op|'('
name|'version'
op|','
name|'version'
op|')'
newline|'\n'
name|'cp'
op|'+='
string|"':build/lucene-core-%s-SNAPSHOT.jar:build/contrib/demo/lucene-demo-%s-SNAPSHOT.jar'"
op|'%'
op|'('
name|'version'
op|'['
op|':'
op|'-'
number|'2'
op|']'
op|','
name|'version'
op|'['
op|':'
op|'-'
number|'2'
op|']'
op|')'
newline|'\n'
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'      '
name|'cp'
op|'='
string|"'build/lucene-core-%s-SNAPSHOT.jar:build/contrib/demo/lucene-demo-%s-SNAPSHOT.jar'"
op|'%'
op|'('
name|'version'
op|','
name|'version'
op|')'
newline|'\n'
dedent|''
name|'docsDir'
op|'='
string|"'src'"
newline|'\n'
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'    '
name|'cp'
op|'='
string|"'lucene-core-%s.jar:contrib/demo/lucene-demo-%s.jar'"
op|'%'
op|'('
name|'version'
op|','
name|'version'
op|')'
newline|'\n'
name|'docsDir'
op|'='
string|"'docs'"
newline|'\n'
dedent|''
name|'run'
op|'('
string|"'export JAVA_HOME=%s; %s/bin/java -cp %s org.apache.lucene.demo.IndexFiles -index index -docs %s'"
op|'%'
op|'('
name|'JAVA5_HOME'
op|','
name|'JAVA5_HOME'
op|','
name|'cp'
op|','
name|'docsDir'
op|')'
op|','
string|"'index.log'"
op|')'
newline|'\n'
name|'run'
op|'('
string|"'export JAVA_HOME=%s; %s/bin/java -cp %s org.apache.lucene.demo.SearchFiles -index index -query lucene'"
op|'%'
op|'('
name|'JAVA5_HOME'
op|','
name|'JAVA5_HOME'
op|','
name|'cp'
op|')'
op|','
string|"'search.log'"
op|')'
newline|'\n'
name|'reMatchingDocs'
op|'='
name|'re'
op|'.'
name|'compile'
op|'('
string|"'(\\d+) total matching documents'"
op|')'
newline|'\n'
name|'m'
op|'='
name|'reMatchingDocs'
op|'.'
name|'search'
op|'('
name|'open'
op|'('
string|"'search.log'"
op|','
string|"'rb'"
op|')'
op|'.'
name|'read'
op|'('
op|')'
op|')'
newline|'\n'
name|'if'
name|'m'
name|'is'
name|'None'
op|':'
newline|'\n'
indent|'    '
name|'raise'
name|'RuntimeError'
op|'('
string|"'lucene demo\\'s SearchFiles found no results'"
op|')'
newline|'\n'
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'    '
name|'numHits'
op|'='
name|'int'
op|'('
name|'m'
op|'.'
name|'group'
op|'('
number|'1'
op|')'
op|')'
newline|'\n'
name|'if'
name|'numHits'
op|'<'
number|'100'
op|':'
newline|'\n'
indent|'      '
name|'raise'
name|'RuntimeError'
op|'('
string|"'lucene demo\\'s SearchFiles found too few results: %s'"
op|'%'
name|'numHits'
op|')'
newline|'\n'
dedent|''
name|'print'
string|'\'      got %d hits for query "lucene"\''
op|'%'
name|'numHits'
newline|'\n'
nl|'\n'
DECL|function|main
dedent|''
dedent|''
name|'def'
name|'main'
op|'('
op|')'
op|':'
newline|'\n'
nl|'\n'
indent|'  '
name|'if'
name|'len'
op|'('
name|'sys'
op|'.'
name|'argv'
op|')'
op|'!='
number|'4'
op|':'
newline|'\n'
indent|'    '
name|'print'
newline|'\n'
name|'print'
string|"'Usage python -u %s BaseURL version tmpDir'"
op|'%'
name|'sys'
op|'.'
name|'argv'
op|'['
number|'0'
op|']'
newline|'\n'
name|'print'
newline|'\n'
name|'sys'
op|'.'
name|'exit'
op|'('
number|'1'
op|')'
newline|'\n'
nl|'\n'
dedent|''
name|'baseURL'
op|'='
name|'sys'
op|'.'
name|'argv'
op|'['
number|'1'
op|']'
newline|'\n'
name|'version'
op|'='
name|'sys'
op|'.'
name|'argv'
op|'['
number|'2'
op|']'
newline|'\n'
name|'tmpDir'
op|'='
name|'os'
op|'.'
name|'path'
op|'.'
name|'abspath'
op|'('
name|'sys'
op|'.'
name|'argv'
op|'['
number|'3'
op|']'
op|')'
newline|'\n'
nl|'\n'
name|'if'
name|'not'
name|'DEBUG'
op|':'
newline|'\n'
indent|'    '
name|'if'
name|'os'
op|'.'
name|'path'
op|'.'
name|'exists'
op|'('
name|'tmpDir'
op|')'
op|':'
newline|'\n'
indent|'      '
name|'raise'
name|'RuntimeError'
op|'('
string|"'temp dir %s exists; please remove first'"
op|'%'
name|'tmpDir'
op|')'
newline|'\n'
dedent|''
name|'os'
op|'.'
name|'makedirs'
op|'('
name|'tmpDir'
op|')'
newline|'\n'
nl|'\n'
dedent|''
name|'lucenePath'
op|'='
name|'None'
newline|'\n'
name|'solrPath'
op|'='
name|'None'
newline|'\n'
name|'print'
string|"'Load release URL...'"
newline|'\n'
name|'for'
name|'text'
op|','
name|'subURL'
name|'in'
name|'getDirEntries'
op|'('
name|'baseURL'
op|')'
op|':'
newline|'\n'
indent|'    '
name|'if'
name|'text'
op|'.'
name|'lower'
op|'('
op|')'
op|'.'
name|'find'
op|'('
string|"'lucene'"
op|')'
op|'!='
op|'-'
number|'1'
op|':'
newline|'\n'
indent|'      '
name|'lucenePath'
op|'='
name|'subURL'
newline|'\n'
dedent|''
name|'elif'
name|'text'
op|'.'
name|'lower'
op|'('
op|')'
op|'.'
name|'find'
op|'('
string|"'solr'"
op|')'
op|'!='
op|'-'
number|'1'
op|':'
newline|'\n'
indent|'      '
name|'solrPath'
op|'='
name|'subURL'
newline|'\n'
nl|'\n'
dedent|''
dedent|''
name|'if'
name|'lucenePath'
name|'is'
name|'None'
op|':'
newline|'\n'
indent|'    '
name|'raise'
name|'RuntimeError'
op|'('
string|"'could not find lucene subdir'"
op|')'
newline|'\n'
dedent|''
name|'if'
name|'solrPath'
name|'is'
name|'None'
op|':'
newline|'\n'
indent|'    '
name|'raise'
name|'RuntimeError'
op|'('
string|"'could not find solr subdir'"
op|')'
newline|'\n'
nl|'\n'
dedent|''
name|'print'
newline|'\n'
name|'print'
string|"'Test Lucene...'"
newline|'\n'
name|'checkSigs'
op|'('
string|"'lucene'"
op|','
name|'lucenePath'
op|','
name|'version'
op|','
name|'tmpDir'
op|')'
newline|'\n'
name|'for'
name|'artifact'
name|'in'
op|'('
string|"'lucene-%s.tgz'"
op|'%'
name|'version'
op|','
string|"'lucene-%s.zip'"
op|'%'
name|'version'
op|')'
op|':'
newline|'\n'
indent|'    '
name|'unpack'
op|'('
string|"'lucene'"
op|','
name|'tmpDir'
op|','
name|'artifact'
op|','
name|'version'
op|')'
newline|'\n'
dedent|''
name|'unpack'
op|'('
string|"'lucene'"
op|','
name|'tmpDir'
op|','
string|"'lucene-%s-src.tgz'"
op|'%'
name|'version'
op|','
name|'version'
op|')'
newline|'\n'
nl|'\n'
name|'print'
newline|'\n'
name|'print'
string|"'Test Solr...'"
newline|'\n'
name|'checkSigs'
op|'('
string|"'solr'"
op|','
name|'solrPath'
op|','
name|'version'
op|','
name|'tmpDir'
op|')'
newline|'\n'
name|'for'
name|'artifact'
name|'in'
op|'('
string|"'apache-solr-%s.tgz'"
op|'%'
name|'version'
op|','
string|"'apache-solr-%s.zip'"
op|'%'
name|'version'
op|')'
op|':'
newline|'\n'
indent|'    '
name|'unpack'
op|'('
string|"'solr'"
op|','
name|'tmpDir'
op|','
name|'artifact'
op|','
name|'version'
op|')'
newline|'\n'
dedent|''
name|'unpack'
op|'('
string|"'solr'"
op|','
name|'tmpDir'
op|','
string|"'apache-solr-%s-src.tgz'"
op|'%'
name|'version'
op|','
name|'version'
op|')'
newline|'\n'
nl|'\n'
dedent|''
name|'if'
name|'__name__'
op|'=='
string|"'__main__'"
op|':'
newline|'\n'
indent|'  '
name|'main'
op|'('
op|')'
newline|'\n'
nl|'\n'
dedent|''
endmarker|''
end_unit
