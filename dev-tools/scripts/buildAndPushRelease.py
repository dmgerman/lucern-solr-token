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
name|'argparse'
newline|'\n'
name|'import'
name|'datetime'
newline|'\n'
name|'import'
name|'re'
newline|'\n'
name|'import'
name|'time'
newline|'\n'
name|'import'
name|'shutil'
newline|'\n'
name|'import'
name|'os'
newline|'\n'
name|'import'
name|'sys'
newline|'\n'
name|'import'
name|'subprocess'
newline|'\n'
name|'import'
name|'textwrap'
newline|'\n'
nl|'\n'
DECL|variable|LOG
name|'LOG'
op|'='
string|"'/tmp/release.log'"
newline|'\n'
nl|'\n'
DECL|function|log
name|'def'
name|'log'
op|'('
name|'msg'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'f'
op|'='
name|'open'
op|'('
name|'LOG'
op|','
name|'mode'
op|'='
string|"'ab'"
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
name|'msg'
op|'.'
name|'encode'
op|'('
string|"'utf-8'"
op|')'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'close'
op|'('
op|')'
newline|'\n'
nl|'\n'
DECL|function|run
dedent|''
name|'def'
name|'run'
op|'('
name|'command'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'log'
op|'('
string|"'\\n\\n%s: RUN: %s\\n'"
op|'%'
op|'('
name|'datetime'
op|'.'
name|'datetime'
op|'.'
name|'now'
op|'('
op|')'
op|','
name|'command'
op|')'
op|')'
newline|'\n'
name|'if'
name|'os'
op|'.'
name|'system'
op|'('
string|"'%s >> %s 2>&1'"
op|'%'
op|'('
name|'command'
op|','
name|'LOG'
op|')'
op|')'
op|':'
newline|'\n'
indent|'    '
name|'msg'
op|'='
string|"'    FAILED: %s [see log %s]'"
op|'%'
op|'('
name|'command'
op|','
name|'LOG'
op|')'
newline|'\n'
name|'print'
op|'('
name|'msg'
op|')'
newline|'\n'
name|'raise'
name|'RuntimeError'
op|'('
name|'msg'
op|')'
newline|'\n'
nl|'\n'
DECL|function|runAndSendGPGPassword
dedent|''
dedent|''
name|'def'
name|'runAndSendGPGPassword'
op|'('
name|'command'
op|','
name|'password'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'p'
op|'='
name|'subprocess'
op|'.'
name|'Popen'
op|'('
name|'command'
op|','
name|'shell'
op|'='
name|'True'
op|','
name|'bufsize'
op|'='
number|'0'
op|','
name|'stdout'
op|'='
name|'subprocess'
op|'.'
name|'PIPE'
op|','
name|'stderr'
op|'='
name|'subprocess'
op|'.'
name|'STDOUT'
op|','
name|'stdin'
op|'='
name|'subprocess'
op|'.'
name|'PIPE'
op|')'
newline|'\n'
name|'f'
op|'='
name|'open'
op|'('
name|'LOG'
op|','
string|"'ab'"
op|')'
newline|'\n'
name|'while'
name|'True'
op|':'
newline|'\n'
indent|'    '
name|'p'
op|'.'
name|'stdout'
op|'.'
name|'flush'
op|'('
op|')'
newline|'\n'
name|'line'
op|'='
name|'p'
op|'.'
name|'stdout'
op|'.'
name|'readline'
op|'('
op|')'
newline|'\n'
name|'if'
name|'len'
op|'('
name|'line'
op|')'
op|'=='
number|'0'
op|':'
newline|'\n'
indent|'      '
name|'break'
newline|'\n'
dedent|''
name|'f'
op|'.'
name|'write'
op|'('
name|'line'
op|')'
newline|'\n'
name|'if'
name|'line'
op|'.'
name|'find'
op|'('
string|"b'Enter GPG keystore password:'"
op|')'
op|'!='
op|'-'
number|'1'
op|':'
newline|'\n'
indent|'      '
name|'time'
op|'.'
name|'sleep'
op|'('
number|'1.0'
op|')'
newline|'\n'
name|'p'
op|'.'
name|'stdin'
op|'.'
name|'write'
op|'('
op|'('
name|'password'
op|'+'
string|"'\\n'"
op|')'
op|'.'
name|'encode'
op|'('
string|"'UTF-8'"
op|')'
op|')'
newline|'\n'
name|'p'
op|'.'
name|'stdin'
op|'.'
name|'write'
op|'('
string|"'\\n'"
op|'.'
name|'encode'
op|'('
string|"'UTF-8'"
op|')'
op|')'
newline|'\n'
nl|'\n'
dedent|''
dedent|''
name|'result'
op|'='
name|'p'
op|'.'
name|'poll'
op|'('
op|')'
newline|'\n'
name|'if'
name|'result'
op|'!='
number|'0'
op|':'
newline|'\n'
indent|'    '
name|'msg'
op|'='
string|"'    FAILED: %s [see log %s]'"
op|'%'
op|'('
name|'command'
op|','
name|'LOG'
op|')'
newline|'\n'
name|'print'
op|'('
name|'msg'
op|')'
newline|'\n'
name|'raise'
name|'RuntimeError'
op|'('
name|'msg'
op|')'
newline|'\n'
nl|'\n'
DECL|function|getGitRev
dedent|''
dedent|''
name|'def'
name|'getGitRev'
op|'('
op|')'
op|':'
newline|'\n'
indent|'  '
name|'status'
op|'='
name|'os'
op|'.'
name|'popen'
op|'('
string|"'git status'"
op|')'
op|'.'
name|'read'
op|'('
op|')'
op|'.'
name|'strip'
op|'('
op|')'
newline|'\n'
name|'if'
string|"'Your branch is up-to-date'"
name|'not'
name|'in'
name|'status'
op|':'
newline|'\n'
indent|'    '
name|'raise'
name|'RuntimeError'
op|'('
string|"'git clone has local changes:\\n\\n%s'"
op|'%'
name|'status'
op|')'
newline|'\n'
dedent|''
name|'if'
string|"'Untracked files'"
name|'in'
name|'status'
op|':'
newline|'\n'
indent|'    '
name|'raise'
name|'RuntimeError'
op|'('
string|"'there are untracked files; please run git clean -xfd:\\n\\n%s'"
op|'%'
name|'status'
op|')'
newline|'\n'
dedent|''
name|'return'
name|'os'
op|'.'
name|'popen'
op|'('
string|"'git rev-parse HEAD'"
op|')'
op|'.'
name|'read'
op|'('
op|')'
op|'.'
name|'strip'
op|'('
op|')'
newline|'\n'
nl|'\n'
nl|'\n'
DECL|function|prepare
dedent|''
name|'def'
name|'prepare'
op|'('
name|'root'
op|','
name|'version'
op|','
name|'gpgKeyID'
op|','
name|'gpgPassword'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'print'
op|'('
op|')'
newline|'\n'
name|'print'
op|'('
string|"'Prepare release...'"
op|')'
newline|'\n'
name|'if'
name|'os'
op|'.'
name|'path'
op|'.'
name|'exists'
op|'('
name|'LOG'
op|')'
op|':'
newline|'\n'
indent|'    '
name|'os'
op|'.'
name|'remove'
op|'('
name|'LOG'
op|')'
newline|'\n'
nl|'\n'
dedent|''
name|'os'
op|'.'
name|'chdir'
op|'('
name|'root'
op|')'
newline|'\n'
name|'print'
op|'('
string|"'  svn up...'"
op|')'
newline|'\n'
name|'run'
op|'('
string|"'svn up'"
op|')'
newline|'\n'
nl|'\n'
name|'rev'
op|'='
name|'getGitRev'
op|'('
op|')'
newline|'\n'
name|'print'
op|'('
string|"'  git rev: %s'"
op|'%'
name|'rev'
op|')'
newline|'\n'
name|'log'
op|'('
string|"'\\nGIT rev: %s\\n'"
op|'%'
name|'rev'
op|')'
newline|'\n'
nl|'\n'
name|'print'
op|'('
string|"'  ant clean test'"
op|')'
newline|'\n'
name|'run'
op|'('
string|"'ant clean test'"
op|')'
newline|'\n'
nl|'\n'
name|'open'
op|'('
string|"'rev.txt'"
op|','
name|'mode'
op|'='
string|"'wb'"
op|')'
op|'.'
name|'write'
op|'('
name|'rev'
op|'.'
name|'encode'
op|'('
string|"'UTF-8'"
op|')'
op|')'
newline|'\n'
nl|'\n'
name|'print'
op|'('
string|"'  lucene prepare-release'"
op|')'
newline|'\n'
name|'os'
op|'.'
name|'chdir'
op|'('
string|"'lucene'"
op|')'
newline|'\n'
name|'cmd'
op|'='
string|"'ant -Dversion=%s'"
op|'%'
name|'version'
newline|'\n'
name|'if'
name|'gpgKeyID'
name|'is'
name|'not'
name|'None'
op|':'
newline|'\n'
indent|'    '
name|'cmd'
op|'+='
string|"' -Dgpg.key=%s prepare-release'"
op|'%'
name|'gpgKeyID'
newline|'\n'
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'    '
name|'cmd'
op|'+='
string|"' prepare-release-no-sign'"
newline|'\n'
nl|'\n'
dedent|''
name|'if'
name|'gpgPassword'
name|'is'
name|'not'
name|'None'
op|':'
newline|'\n'
indent|'    '
name|'runAndSendGPGPassword'
op|'('
name|'cmd'
op|','
name|'gpgPassword'
op|')'
newline|'\n'
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'    '
name|'run'
op|'('
name|'cmd'
op|')'
newline|'\n'
nl|'\n'
dedent|''
name|'print'
op|'('
string|"'  solr prepare-release'"
op|')'
newline|'\n'
name|'os'
op|'.'
name|'chdir'
op|'('
string|"'../solr'"
op|')'
newline|'\n'
name|'cmd'
op|'='
string|"'ant -Dversion=%s'"
op|'%'
name|'version'
newline|'\n'
name|'if'
name|'gpgKeyID'
name|'is'
name|'not'
name|'None'
op|':'
newline|'\n'
indent|'    '
name|'cmd'
op|'+='
string|"' -Dgpg.key=%s prepare-release'"
op|'%'
name|'gpgKeyID'
newline|'\n'
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'    '
name|'cmd'
op|'+='
string|"' prepare-release-no-sign'"
newline|'\n'
nl|'\n'
dedent|''
name|'if'
name|'gpgPassword'
name|'is'
name|'not'
name|'None'
op|':'
newline|'\n'
indent|'    '
name|'runAndSendGPGPassword'
op|'('
name|'cmd'
op|','
name|'gpgPassword'
op|')'
newline|'\n'
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'    '
name|'run'
op|'('
name|'cmd'
op|')'
newline|'\n'
nl|'\n'
dedent|''
name|'print'
op|'('
string|"'  done!'"
op|')'
newline|'\n'
name|'print'
op|'('
op|')'
newline|'\n'
name|'return'
name|'rev'
newline|'\n'
nl|'\n'
DECL|function|push
dedent|''
name|'def'
name|'push'
op|'('
name|'version'
op|','
name|'root'
op|','
name|'rev'
op|','
name|'rcNum'
op|','
name|'username'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'print'
op|'('
string|"'Push...'"
op|')'
newline|'\n'
name|'dir'
op|'='
string|"'lucene-solr-%s-RC%d-rev%s'"
op|'%'
op|'('
name|'version'
op|','
name|'rcNum'
op|','
name|'rev'
op|')'
newline|'\n'
name|'s'
op|'='
name|'os'
op|'.'
name|'popen'
op|'('
string|'\'ssh %s@people.apache.org "ls -ld public_html/staging_area/%s" 2>&1\''
op|'%'
op|'('
name|'username'
op|','
name|'dir'
op|')'
op|')'
op|'.'
name|'read'
op|'('
op|')'
newline|'\n'
name|'if'
string|"'no such file or directory'"
name|'not'
name|'in'
name|'s'
op|'.'
name|'lower'
op|'('
op|')'
op|':'
newline|'\n'
indent|'    '
name|'print'
op|'('
string|"'  Remove old dir...'"
op|')'
newline|'\n'
name|'run'
op|'('
string|'\'ssh %s@people.apache.org "chmod -R u+rwX public_html/staging_area/%s; rm -rf public_html/staging_area/%s"\''
op|'%'
nl|'\n'
op|'('
name|'username'
op|','
name|'dir'
op|','
name|'dir'
op|')'
op|')'
newline|'\n'
dedent|''
name|'run'
op|'('
string|'\'ssh %s@people.apache.org "mkdir -p public_html/staging_area/%s/lucene public_html/staging_area/%s/solr"\''
op|'%'
op|'('
name|'username'
op|','
name|'dir'
op|','
name|'dir'
op|')'
op|')'
newline|'\n'
name|'print'
op|'('
string|"'  Lucene'"
op|')'
newline|'\n'
name|'os'
op|'.'
name|'chdir'
op|'('
string|"'%s/lucene/dist'"
op|'%'
name|'root'
op|')'
newline|'\n'
name|'print'
op|'('
string|"'    zip...'"
op|')'
newline|'\n'
name|'if'
name|'os'
op|'.'
name|'path'
op|'.'
name|'exists'
op|'('
string|"'lucene.tar.bz2'"
op|')'
op|':'
newline|'\n'
indent|'    '
name|'os'
op|'.'
name|'remove'
op|'('
string|"'lucene.tar.bz2'"
op|')'
newline|'\n'
dedent|''
name|'run'
op|'('
string|"'tar cjf lucene.tar.bz2 *'"
op|')'
newline|'\n'
name|'print'
op|'('
string|"'    copy...'"
op|')'
newline|'\n'
name|'run'
op|'('
string|"'scp lucene.tar.bz2 %s@people.apache.org:public_html/staging_area/%s/lucene'"
op|'%'
op|'('
name|'username'
op|','
name|'dir'
op|')'
op|')'
newline|'\n'
name|'print'
op|'('
string|"'    unzip...'"
op|')'
newline|'\n'
name|'run'
op|'('
string|'\'ssh %s@people.apache.org "cd public_html/staging_area/%s/lucene; tar xjf lucene.tar.bz2; rm -f lucene.tar.bz2"\''
op|'%'
op|'('
name|'username'
op|','
name|'dir'
op|')'
op|')'
newline|'\n'
name|'os'
op|'.'
name|'remove'
op|'('
string|"'lucene.tar.bz2'"
op|')'
newline|'\n'
nl|'\n'
name|'print'
op|'('
string|"'  Solr'"
op|')'
newline|'\n'
name|'os'
op|'.'
name|'chdir'
op|'('
string|"'%s/solr/package'"
op|'%'
name|'root'
op|')'
newline|'\n'
name|'print'
op|'('
string|"'    zip...'"
op|')'
newline|'\n'
name|'if'
name|'os'
op|'.'
name|'path'
op|'.'
name|'exists'
op|'('
string|"'solr.tar.bz2'"
op|')'
op|':'
newline|'\n'
indent|'    '
name|'os'
op|'.'
name|'remove'
op|'('
string|"'solr.tar.bz2'"
op|')'
newline|'\n'
dedent|''
name|'run'
op|'('
string|"'tar cjf solr.tar.bz2 *'"
op|')'
newline|'\n'
name|'print'
op|'('
string|"'    copy...'"
op|')'
newline|'\n'
name|'run'
op|'('
string|"'scp solr.tar.bz2 %s@people.apache.org:public_html/staging_area/%s/solr'"
op|'%'
op|'('
name|'username'
op|','
name|'dir'
op|')'
op|')'
newline|'\n'
name|'print'
op|'('
string|"'    unzip...'"
op|')'
newline|'\n'
name|'run'
op|'('
string|'\'ssh %s@people.apache.org "cd public_html/staging_area/%s/solr; tar xjf solr.tar.bz2; rm -f solr.tar.bz2"\''
op|'%'
op|'('
name|'username'
op|','
name|'dir'
op|')'
op|')'
newline|'\n'
name|'os'
op|'.'
name|'remove'
op|'('
string|"'solr.tar.bz2'"
op|')'
newline|'\n'
nl|'\n'
name|'print'
op|'('
string|"'  chmod...'"
op|')'
newline|'\n'
name|'run'
op|'('
string|'\'ssh %s@people.apache.org "chmod -R a+rX-w public_html/staging_area/%s"\''
op|'%'
op|'('
name|'username'
op|','
name|'dir'
op|')'
op|')'
newline|'\n'
nl|'\n'
name|'print'
op|'('
string|"'  done!'"
op|')'
newline|'\n'
name|'url'
op|'='
string|"'http://people.apache.org/~%s/staging_area/%s'"
op|'%'
op|'('
name|'username'
op|','
name|'dir'
op|')'
newline|'\n'
name|'return'
name|'url'
newline|'\n'
nl|'\n'
DECL|function|pushLocal
dedent|''
name|'def'
name|'pushLocal'
op|'('
name|'version'
op|','
name|'root'
op|','
name|'rev'
op|','
name|'rcNum'
op|','
name|'localDir'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'print'
op|'('
string|"'Push local [%s]...'"
op|'%'
name|'localDir'
op|')'
newline|'\n'
name|'os'
op|'.'
name|'makedirs'
op|'('
name|'localDir'
op|')'
newline|'\n'
nl|'\n'
name|'dir'
op|'='
string|"'lucene-solr-%s-RC%d-rev%s'"
op|'%'
op|'('
name|'version'
op|','
name|'rcNum'
op|','
name|'rev'
op|')'
newline|'\n'
name|'os'
op|'.'
name|'makedirs'
op|'('
string|"'%s/%s/lucene'"
op|'%'
op|'('
name|'localDir'
op|','
name|'dir'
op|')'
op|')'
newline|'\n'
name|'os'
op|'.'
name|'makedirs'
op|'('
string|"'%s/%s/solr'"
op|'%'
op|'('
name|'localDir'
op|','
name|'dir'
op|')'
op|')'
newline|'\n'
name|'print'
op|'('
string|"'  Lucene'"
op|')'
newline|'\n'
name|'os'
op|'.'
name|'chdir'
op|'('
string|"'%s/lucene/dist'"
op|'%'
name|'root'
op|')'
newline|'\n'
name|'print'
op|'('
string|"'    zip...'"
op|')'
newline|'\n'
name|'if'
name|'os'
op|'.'
name|'path'
op|'.'
name|'exists'
op|'('
string|"'lucene.tar.bz2'"
op|')'
op|':'
newline|'\n'
indent|'    '
name|'os'
op|'.'
name|'remove'
op|'('
string|"'lucene.tar.bz2'"
op|')'
newline|'\n'
dedent|''
name|'run'
op|'('
string|"'tar cjf lucene.tar.bz2 *'"
op|')'
newline|'\n'
nl|'\n'
name|'os'
op|'.'
name|'chdir'
op|'('
string|"'%s/%s/lucene'"
op|'%'
op|'('
name|'localDir'
op|','
name|'dir'
op|')'
op|')'
newline|'\n'
name|'print'
op|'('
string|"'    unzip...'"
op|')'
newline|'\n'
name|'run'
op|'('
string|'\'tar xjf "%s/lucene/dist/lucene.tar.bz2"\''
op|'%'
name|'root'
op|')'
newline|'\n'
name|'os'
op|'.'
name|'remove'
op|'('
string|"'%s/lucene/dist/lucene.tar.bz2'"
op|'%'
name|'root'
op|')'
newline|'\n'
nl|'\n'
name|'print'
op|'('
string|"'  Solr'"
op|')'
newline|'\n'
name|'os'
op|'.'
name|'chdir'
op|'('
string|"'%s/solr/package'"
op|'%'
name|'root'
op|')'
newline|'\n'
name|'print'
op|'('
string|"'    zip...'"
op|')'
newline|'\n'
name|'if'
name|'os'
op|'.'
name|'path'
op|'.'
name|'exists'
op|'('
string|"'solr.tar.bz2'"
op|')'
op|':'
newline|'\n'
indent|'    '
name|'os'
op|'.'
name|'remove'
op|'('
string|"'solr.tar.bz2'"
op|')'
newline|'\n'
dedent|''
name|'run'
op|'('
string|"'tar cjf solr.tar.bz2 *'"
op|')'
newline|'\n'
name|'print'
op|'('
string|"'    unzip...'"
op|')'
newline|'\n'
name|'os'
op|'.'
name|'chdir'
op|'('
string|"'%s/%s/solr'"
op|'%'
op|'('
name|'localDir'
op|','
name|'dir'
op|')'
op|')'
newline|'\n'
name|'run'
op|'('
string|'\'tar xjf "%s/solr/package/solr.tar.bz2"\''
op|'%'
name|'root'
op|')'
newline|'\n'
name|'os'
op|'.'
name|'remove'
op|'('
string|"'%s/solr/package/solr.tar.bz2'"
op|'%'
name|'root'
op|')'
newline|'\n'
nl|'\n'
name|'print'
op|'('
string|"'  KEYS'"
op|')'
newline|'\n'
name|'run'
op|'('
string|"'wget http://people.apache.org/keys/group/lucene.asc'"
op|')'
newline|'\n'
name|'os'
op|'.'
name|'rename'
op|'('
string|"'lucene.asc'"
op|','
string|"'KEYS'"
op|')'
newline|'\n'
name|'run'
op|'('
string|"'chmod a+r-w KEYS'"
op|')'
newline|'\n'
name|'run'
op|'('
string|"'cp KEYS ../lucene'"
op|')'
newline|'\n'
nl|'\n'
name|'print'
op|'('
string|"'  chmod...'"
op|')'
newline|'\n'
name|'os'
op|'.'
name|'chdir'
op|'('
string|"'..'"
op|')'
newline|'\n'
name|'run'
op|'('
string|"'chmod -R a+rX-w .'"
op|')'
newline|'\n'
nl|'\n'
name|'print'
op|'('
string|"'  done!'"
op|')'
newline|'\n'
name|'return'
string|"'file://%s/%s'"
op|'%'
op|'('
name|'os'
op|'.'
name|'path'
op|'.'
name|'abspath'
op|'('
name|'localDir'
op|')'
op|','
name|'dir'
op|')'
newline|'\n'
nl|'\n'
DECL|function|read_version
dedent|''
name|'def'
name|'read_version'
op|'('
name|'path'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'version_props_file'
op|'='
name|'os'
op|'.'
name|'path'
op|'.'
name|'join'
op|'('
name|'path'
op|','
string|"'lucene'"
op|','
string|"'version.properties'"
op|')'
newline|'\n'
name|'return'
name|'re'
op|'.'
name|'search'
op|'('
string|"r'version\\.base=(.*)'"
op|','
name|'open'
op|'('
name|'version_props_file'
op|')'
op|'.'
name|'read'
op|'('
op|')'
op|')'
op|'.'
name|'group'
op|'('
number|'1'
op|')'
newline|'\n'
nl|'\n'
DECL|function|parse_config
dedent|''
name|'def'
name|'parse_config'
op|'('
op|')'
op|':'
newline|'\n'
indent|'  '
name|'epilogue'
op|'='
name|'textwrap'
op|'.'
name|'dedent'
op|'('
string|"'''\n    Example usage for a Release Manager:\n    python3.2 -u buildAndPushRelease.py --push-remote mikemccand --sign 6E68DA61 --rc-num 1 --version 4.7.0 /path/to/lucene_solr_4_7\n  '''"
op|')'
newline|'\n'
name|'description'
op|'='
string|"'Utility to build, push, and test a release.'"
newline|'\n'
name|'parser'
op|'='
name|'argparse'
op|'.'
name|'ArgumentParser'
op|'('
name|'description'
op|'='
name|'description'
op|','
name|'epilog'
op|'='
name|'epilogue'
op|','
nl|'\n'
name|'formatter_class'
op|'='
name|'argparse'
op|'.'
name|'RawDescriptionHelpFormatter'
op|')'
newline|'\n'
name|'parser'
op|'.'
name|'add_argument'
op|'('
string|"'--no-prepare'"
op|','
name|'dest'
op|'='
string|"'prepare'"
op|','
name|'default'
op|'='
name|'True'
op|','
name|'action'
op|'='
string|"'store_false'"
op|','
nl|'\n'
name|'help'
op|'='
string|"'Use the already built release in the provided checkout'"
op|')'
newline|'\n'
name|'parser'
op|'.'
name|'add_argument'
op|'('
string|"'--push-remote'"
op|','
name|'metavar'
op|'='
string|"'USERNAME'"
op|','
nl|'\n'
name|'help'
op|'='
string|"'Push the release to people.apache.org for the given user'"
op|')'
newline|'\n'
name|'parser'
op|'.'
name|'add_argument'
op|'('
string|"'--push-local'"
op|','
name|'metavar'
op|'='
string|"'PATH'"
op|','
nl|'\n'
name|'help'
op|'='
string|"'Push the release to the local path'"
op|')'
newline|'\n'
name|'parser'
op|'.'
name|'add_argument'
op|'('
string|"'--sign'"
op|','
name|'metavar'
op|'='
string|"'KEYID'"
op|','
nl|'\n'
name|'help'
op|'='
string|"'Sign the release with the given gpg key'"
op|')'
newline|'\n'
name|'parser'
op|'.'
name|'add_argument'
op|'('
string|"'--rc-num'"
op|','
name|'metavar'
op|'='
string|"'NUM'"
op|','
name|'type'
op|'='
name|'int'
op|','
name|'default'
op|'='
number|'1'
op|','
nl|'\n'
name|'help'
op|'='
string|"'Release Candidate number, required'"
op|')'
newline|'\n'
name|'parser'
op|'.'
name|'add_argument'
op|'('
string|"'--smoke-test'"
op|','
name|'metavar'
op|'='
string|"'PATH'"
op|','
nl|'\n'
name|'help'
op|'='
string|"'Run the smoker tester on the release in the given directory'"
op|')'
newline|'\n'
name|'parser'
op|'.'
name|'add_argument'
op|'('
string|"'root'"
op|','
name|'metavar'
op|'='
string|"'checkout_path'"
op|','
nl|'\n'
name|'help'
op|'='
string|"'Root of SVN checkout for lucene-solr'"
op|')'
newline|'\n'
name|'config'
op|'='
name|'parser'
op|'.'
name|'parse_args'
op|'('
op|')'
newline|'\n'
nl|'\n'
name|'if'
name|'config'
op|'.'
name|'push_remote'
name|'is'
name|'not'
name|'None'
name|'and'
name|'config'
op|'.'
name|'push_local'
name|'is'
name|'not'
name|'None'
op|':'
newline|'\n'
indent|'    '
name|'parser'
op|'.'
name|'error'
op|'('
string|"'Cannot specify --push-remote and --push-local together'"
op|')'
newline|'\n'
dedent|''
name|'if'
name|'not'
name|'config'
op|'.'
name|'prepare'
name|'and'
name|'config'
op|'.'
name|'sign'
op|':'
newline|'\n'
indent|'    '
name|'parser'
op|'.'
name|'error'
op|'('
string|"'Cannot sign already built release'"
op|')'
newline|'\n'
dedent|''
name|'if'
name|'config'
op|'.'
name|'push_local'
name|'is'
name|'not'
name|'None'
name|'and'
name|'os'
op|'.'
name|'path'
op|'.'
name|'exists'
op|'('
name|'config'
op|'.'
name|'push_local'
op|')'
op|':'
newline|'\n'
indent|'    '
name|'parser'
op|'.'
name|'error'
op|'('
string|"'Cannot push to local path that already exists'"
op|')'
newline|'\n'
dedent|''
name|'if'
name|'config'
op|'.'
name|'rc_num'
op|'<='
number|'0'
op|':'
newline|'\n'
indent|'    '
name|'parser'
op|'.'
name|'error'
op|'('
string|"'Release Candidate number must be a positive integer'"
op|')'
newline|'\n'
dedent|''
name|'if'
name|'not'
name|'os'
op|'.'
name|'path'
op|'.'
name|'isdir'
op|'('
name|'config'
op|'.'
name|'root'
op|')'
op|':'
newline|'\n'
comment|'# TODO: add additional svn check to ensure dir is a real lucene-solr checkout'
nl|'\n'
indent|'    '
name|'parser'
op|'.'
name|'error'
op|'('
string|"'Root path is not a valid lucene-solr checkout'"
op|')'
newline|'\n'
nl|'\n'
dedent|''
name|'config'
op|'.'
name|'version'
op|'='
name|'read_version'
op|'('
name|'config'
op|'.'
name|'root'
op|')'
newline|'\n'
name|'print'
op|'('
string|"'Building version: %s'"
op|'%'
name|'config'
op|'.'
name|'version'
op|')'
newline|'\n'
nl|'\n'
name|'if'
name|'config'
op|'.'
name|'sign'
op|':'
newline|'\n'
indent|'    '
name|'sys'
op|'.'
name|'stdout'
op|'.'
name|'flush'
op|'('
op|')'
newline|'\n'
name|'import'
name|'getpass'
newline|'\n'
name|'config'
op|'.'
name|'key_id'
op|'='
name|'config'
op|'.'
name|'sign'
newline|'\n'
name|'config'
op|'.'
name|'key_password'
op|'='
name|'getpass'
op|'.'
name|'getpass'
op|'('
string|"'Enter GPG keystore password: '"
op|')'
newline|'\n'
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'    '
name|'config'
op|'.'
name|'gpg_password'
op|'='
name|'None'
newline|'\n'
nl|'\n'
dedent|''
name|'return'
name|'config'
newline|'\n'
nl|'\n'
DECL|function|main
dedent|''
name|'def'
name|'main'
op|'('
op|')'
op|':'
newline|'\n'
indent|'  '
name|'c'
op|'='
name|'parse_config'
op|'('
op|')'
newline|'\n'
nl|'\n'
name|'if'
name|'c'
op|'.'
name|'prepare'
op|':'
newline|'\n'
indent|'    '
name|'rev'
op|'='
name|'prepare'
op|'('
name|'c'
op|'.'
name|'root'
op|','
name|'c'
op|'.'
name|'version'
op|','
name|'c'
op|'.'
name|'key_id'
op|','
name|'c'
op|'.'
name|'key_password'
op|')'
newline|'\n'
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'    '
name|'os'
op|'.'
name|'chdir'
op|'('
name|'root'
op|')'
newline|'\n'
name|'rev'
op|'='
name|'open'
op|'('
string|"'rev.txt'"
op|','
name|'encoding'
op|'='
string|"'UTF-8'"
op|')'
op|'.'
name|'read'
op|'('
op|')'
newline|'\n'
nl|'\n'
dedent|''
name|'if'
name|'c'
op|'.'
name|'push_remote'
op|':'
newline|'\n'
indent|'    '
name|'url'
op|'='
name|'push'
op|'('
name|'c'
op|'.'
name|'version'
op|','
name|'c'
op|'.'
name|'root'
op|','
name|'rev'
op|','
name|'c'
op|'.'
name|'rc_num'
op|','
name|'c'
op|'.'
name|'push_remote'
op|')'
newline|'\n'
dedent|''
name|'elif'
name|'c'
op|'.'
name|'push_local'
op|':'
newline|'\n'
indent|'    '
name|'url'
op|'='
name|'pushLocal'
op|'('
name|'c'
op|'.'
name|'version'
op|','
name|'c'
op|'.'
name|'root'
op|','
name|'rev'
op|','
name|'c'
op|'.'
name|'rc_num'
op|','
name|'c'
op|'.'
name|'push_local'
op|')'
newline|'\n'
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'    '
name|'url'
op|'='
name|'None'
newline|'\n'
nl|'\n'
dedent|''
name|'if'
name|'url'
name|'is'
name|'not'
name|'None'
op|':'
newline|'\n'
indent|'    '
name|'print'
op|'('
string|"'  URL: %s'"
op|'%'
name|'url'
op|')'
newline|'\n'
name|'print'
op|'('
string|"'Next set the PYTHON_EXEC env var and you can run the smoker tester:'"
op|')'
newline|'\n'
name|'p'
op|'='
name|'re'
op|'.'
name|'compile'
op|'('
string|'"(.*)\\/"'
op|')'
newline|'\n'
name|'m'
op|'='
name|'p'
op|'.'
name|'match'
op|'('
name|'sys'
op|'.'
name|'argv'
op|'['
number|'0'
op|']'
op|')'
newline|'\n'
name|'print'
op|'('
string|"' $PYTHON_EXEC %ssmokeTestRelease.py %s'"
op|'%'
op|'('
name|'m'
op|'.'
name|'group'
op|'('
op|')'
op|','
name|'url'
op|')'
op|')'
newline|'\n'
nl|'\n'
dedent|''
dedent|''
name|'if'
name|'__name__'
op|'=='
string|"'__main__'"
op|':'
newline|'\n'
indent|'  '
name|'try'
op|':'
newline|'\n'
indent|'    '
name|'main'
op|'('
op|')'
newline|'\n'
dedent|''
name|'except'
name|'KeyboardInterrupt'
op|':'
newline|'\n'
indent|'    '
name|'print'
op|'('
string|"'Keyboard interrupt...exiting'"
op|')'
newline|'\n'
nl|'\n'
dedent|''
dedent|''
endmarker|''
end_unit
