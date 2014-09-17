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
name|'io'
newline|'\n'
name|'import'
name|'os'
newline|'\n'
name|'import'
name|'re'
newline|'\n'
name|'import'
name|'subprocess'
newline|'\n'
name|'import'
name|'sys'
newline|'\n'
nl|'\n'
DECL|class|Version
name|'class'
name|'Version'
op|'('
name|'object'
op|')'
op|':'
newline|'\n'
DECL|member|__init__
indent|'  '
name|'def'
name|'__init__'
op|'('
name|'self'
op|','
name|'major'
op|','
name|'minor'
op|','
name|'bugfix'
op|')'
op|':'
newline|'\n'
indent|'    '
name|'self'
op|'.'
name|'major'
op|'='
name|'major'
newline|'\n'
name|'self'
op|'.'
name|'minor'
op|'='
name|'minor'
newline|'\n'
name|'self'
op|'.'
name|'bugfix'
op|'='
name|'bugfix'
newline|'\n'
name|'self'
op|'.'
name|'previous_dot_matcher'
op|'='
name|'self'
op|'.'
name|'make_previous_matcher'
op|'('
op|')'
newline|'\n'
name|'self'
op|'.'
name|'dot'
op|'='
string|"'%d.%d.%d'"
op|'%'
op|'('
name|'self'
op|'.'
name|'major'
op|','
name|'self'
op|'.'
name|'minor'
op|','
name|'self'
op|'.'
name|'bugfix'
op|')'
newline|'\n'
name|'self'
op|'.'
name|'constant'
op|'='
string|"'LUCENE_%d_%d_%d'"
op|'%'
op|'('
name|'self'
op|'.'
name|'major'
op|','
name|'self'
op|'.'
name|'minor'
op|','
name|'self'
op|'.'
name|'bugfix'
op|')'
newline|'\n'
nl|'\n'
dedent|''
op|'@'
name|'classmethod'
newline|'\n'
DECL|member|parse
name|'def'
name|'parse'
op|'('
name|'cls'
op|','
name|'value'
op|')'
op|':'
newline|'\n'
indent|'    '
name|'match'
op|'='
name|'re'
op|'.'
name|'search'
op|'('
string|"r'(\\d+)\\.(\\d+).(\\d+)'"
op|','
name|'value'
op|')'
newline|'\n'
name|'if'
name|'match'
name|'is'
name|'None'
op|':'
newline|'\n'
indent|'      '
name|'raise'
name|'argparse'
op|'.'
name|'ArgumentTypeError'
op|'('
string|"'Version argument must be of format x.y.z'"
op|')'
newline|'\n'
dedent|''
name|'return'
name|'Version'
op|'('
op|'*'
op|'['
name|'int'
op|'('
name|'v'
op|')'
name|'for'
name|'v'
name|'in'
name|'match'
op|'.'
name|'groups'
op|'('
op|')'
op|']'
op|')'
newline|'\n'
nl|'\n'
DECL|member|__str__
dedent|''
name|'def'
name|'__str__'
op|'('
name|'self'
op|')'
op|':'
newline|'\n'
indent|'    '
name|'return'
name|'self'
op|'.'
name|'dot'
newline|'\n'
nl|'\n'
DECL|member|make_previous_matcher
dedent|''
name|'def'
name|'make_previous_matcher'
op|'('
name|'self'
op|','
name|'prefix'
op|'='
string|"''"
op|','
name|'suffix'
op|'='
string|"''"
op|','
name|'sep'
op|'='
string|"'\\\\.'"
op|')'
op|':'
newline|'\n'
indent|'    '
name|'if'
name|'self'
op|'.'
name|'is_bugfix_release'
op|'('
op|')'
op|':'
newline|'\n'
indent|'      '
name|'pattern'
op|'='
string|"'%s%s%s%s%d'"
op|'%'
op|'('
name|'self'
op|'.'
name|'major'
op|','
name|'sep'
op|','
name|'self'
op|'.'
name|'minor'
op|','
name|'sep'
op|','
name|'self'
op|'.'
name|'bugfix'
op|'-'
number|'1'
op|')'
newline|'\n'
dedent|''
name|'elif'
name|'self'
op|'.'
name|'is_minor_release'
op|'('
op|')'
op|':'
newline|'\n'
indent|'      '
name|'pattern'
op|'='
string|"'%s%s%d%s\\\\d+'"
op|'%'
op|'('
name|'self'
op|'.'
name|'major'
op|','
name|'sep'
op|','
name|'self'
op|'.'
name|'minor'
op|'-'
number|'1'
op|','
name|'sep'
op|')'
newline|'\n'
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'      '
name|'pattern'
op|'='
string|"'%d%s\\\\d+%s\\\\d+'"
op|'%'
op|'('
name|'self'
op|'.'
name|'major'
op|'-'
number|'1'
op|','
name|'sep'
op|','
name|'sep'
op|')'
newline|'\n'
nl|'\n'
dedent|''
name|'return'
name|'re'
op|'.'
name|'compile'
op|'('
name|'prefix'
op|'+'
string|"'('"
op|'+'
name|'pattern'
op|'+'
string|"')'"
op|'+'
name|'suffix'
op|')'
newline|'\n'
nl|'\n'
DECL|member|is_bugfix_release
dedent|''
name|'def'
name|'is_bugfix_release'
op|'('
name|'self'
op|')'
op|':'
newline|'\n'
indent|'    '
name|'return'
name|'self'
op|'.'
name|'bugfix'
op|'!='
number|'0'
newline|'\n'
nl|'\n'
DECL|member|is_minor_release
dedent|''
name|'def'
name|'is_minor_release'
op|'('
name|'self'
op|')'
op|':'
newline|'\n'
indent|'    '
name|'return'
name|'self'
op|'.'
name|'bugfix'
op|'=='
number|'0'
name|'and'
name|'self'
op|'.'
name|'minor'
op|'!='
number|'0'
newline|'\n'
nl|'\n'
DECL|member|is_major_release
dedent|''
name|'def'
name|'is_major_release'
op|'('
name|'self'
op|')'
op|':'
newline|'\n'
indent|'    '
name|'return'
name|'self'
op|'.'
name|'bugfix'
op|'=='
number|'0'
name|'and'
name|'self'
op|'.'
name|'minor'
op|'=='
number|'0'
newline|'\n'
nl|'\n'
DECL|function|run
dedent|''
dedent|''
name|'def'
name|'run'
op|'('
name|'cmd'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'try'
op|':'
newline|'\n'
indent|'    '
name|'subprocess'
op|'.'
name|'check_output'
op|'('
name|'cmd'
op|','
name|'shell'
op|'='
name|'True'
op|','
name|'stderr'
op|'='
name|'subprocess'
op|'.'
name|'STDOUT'
op|')'
newline|'\n'
dedent|''
name|'except'
name|'subprocess'
op|'.'
name|'CalledProcessError'
name|'as'
name|'e'
op|':'
newline|'\n'
indent|'    '
name|'print'
op|'('
name|'e'
op|'.'
name|'output'
op|'.'
name|'decode'
op|'('
string|"'utf-8'"
op|')'
op|')'
newline|'\n'
name|'raise'
name|'e'
newline|'\n'
nl|'\n'
DECL|function|update_file
dedent|''
dedent|''
name|'def'
name|'update_file'
op|'('
name|'filename'
op|','
name|'line_re'
op|','
name|'edit'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'infile'
op|'='
name|'open'
op|'('
name|'filename'
op|','
string|"'r'"
op|')'
newline|'\n'
name|'buffer'
op|'='
op|'['
op|']'
newline|'\n'
nl|'\n'
name|'changed'
op|'='
name|'False'
newline|'\n'
name|'for'
name|'line'
name|'in'
name|'infile'
op|':'
newline|'\n'
indent|'    '
name|'if'
name|'not'
name|'changed'
op|':'
newline|'\n'
indent|'      '
name|'match'
op|'='
name|'line_re'
op|'.'
name|'search'
op|'('
name|'line'
op|')'
newline|'\n'
name|'if'
name|'match'
op|':'
newline|'\n'
indent|'        '
name|'changed'
op|'='
name|'edit'
op|'('
name|'buffer'
op|','
name|'match'
op|','
name|'line'
op|')'
newline|'\n'
name|'if'
name|'changed'
name|'is'
name|'None'
op|':'
newline|'\n'
indent|'          '
name|'return'
name|'False'
newline|'\n'
dedent|''
name|'continue'
newline|'\n'
dedent|''
dedent|''
name|'buffer'
op|'.'
name|'append'
op|'('
name|'line'
op|')'
newline|'\n'
dedent|''
name|'if'
name|'not'
name|'changed'
op|':'
newline|'\n'
indent|'    '
name|'raise'
name|'Exception'
op|'('
string|"'Could not find %s in %s'"
op|'%'
op|'('
name|'line_re'
op|','
name|'filename'
op|')'
op|')'
newline|'\n'
dedent|''
name|'with'
name|'open'
op|'('
name|'filename'
op|','
string|"'w'"
op|')'
name|'as'
name|'f'
op|':'
newline|'\n'
indent|'    '
name|'f'
op|'.'
name|'write'
op|'('
string|"''"
op|'.'
name|'join'
op|'('
name|'buffer'
op|')'
op|')'
newline|'\n'
dedent|''
name|'return'
name|'True'
newline|'\n'
nl|'\n'
DECL|function|update_changes
dedent|''
name|'def'
name|'update_changes'
op|'('
name|'filename'
op|','
name|'new_version'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'print'
op|'('
string|"'  adding new section to %s...'"
op|'%'
name|'filename'
op|','
name|'end'
op|'='
string|"''"
op|')'
newline|'\n'
name|'matcher'
op|'='
name|'re'
op|'.'
name|'compile'
op|'('
string|"r'\\d+\\.\\d+\\.\\d+\\s+==='"
op|')'
newline|'\n'
DECL|function|edit
name|'def'
name|'edit'
op|'('
name|'buffer'
op|','
name|'match'
op|','
name|'line'
op|')'
op|':'
newline|'\n'
indent|'    '
name|'if'
name|'new_version'
op|'.'
name|'dot'
name|'in'
name|'line'
op|':'
newline|'\n'
indent|'      '
name|'return'
name|'None'
newline|'\n'
dedent|''
name|'match'
op|'='
name|'new_version'
op|'.'
name|'previous_dot_matcher'
op|'.'
name|'search'
op|'('
name|'line'
op|')'
newline|'\n'
name|'if'
name|'match'
name|'is'
name|'not'
name|'None'
op|':'
newline|'\n'
indent|'      '
name|'buffer'
op|'.'
name|'append'
op|'('
name|'line'
op|'.'
name|'replace'
op|'('
name|'match'
op|'.'
name|'group'
op|'('
number|'0'
op|')'
op|','
name|'new_version'
op|'.'
name|'dot'
op|')'
op|')'
newline|'\n'
name|'buffer'
op|'.'
name|'append'
op|'('
string|"'(No Changes)\\n\\n'"
op|')'
newline|'\n'
dedent|''
name|'buffer'
op|'.'
name|'append'
op|'('
name|'line'
op|')'
newline|'\n'
name|'return'
name|'match'
name|'is'
name|'not'
name|'None'
newline|'\n'
nl|'\n'
dedent|''
name|'changed'
op|'='
name|'update_file'
op|'('
name|'filename'
op|','
name|'matcher'
op|','
name|'edit'
op|')'
newline|'\n'
name|'print'
op|'('
string|"'done'"
name|'if'
name|'changed'
name|'else'
string|"'uptodate'"
op|')'
newline|'\n'
nl|'\n'
DECL|function|add_constant
dedent|''
name|'def'
name|'add_constant'
op|'('
name|'new_version'
op|','
name|'deprecate'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'filename'
op|'='
string|"'lucene/core/src/java/org/apache/lucene/util/Version.java'"
newline|'\n'
name|'print'
op|'('
string|"'  adding constant %s...'"
op|'%'
name|'new_version'
op|'.'
name|'constant'
op|','
name|'end'
op|'='
string|"''"
op|')'
newline|'\n'
name|'constant_prefix'
op|'='
string|"'public static final Version LUCENE_'"
newline|'\n'
name|'matcher'
op|'='
name|'re'
op|'.'
name|'compile'
op|'('
name|'constant_prefix'
op|')'
newline|'\n'
name|'prev_matcher'
op|'='
name|'new_version'
op|'.'
name|'make_previous_matcher'
op|'('
name|'prefix'
op|'='
name|'constant_prefix'
op|','
name|'sep'
op|'='
string|"'_'"
op|')'
newline|'\n'
nl|'\n'
DECL|function|ensure_deprecated
name|'def'
name|'ensure_deprecated'
op|'('
name|'buffer'
op|')'
op|':'
newline|'\n'
indent|'    '
name|'last'
op|'='
name|'buffer'
op|'['
op|'-'
number|'1'
op|']'
newline|'\n'
name|'if'
name|'last'
op|'.'
name|'strip'
op|'('
op|')'
op|'!='
string|"'@Deprecated'"
op|':'
newline|'\n'
indent|'      '
name|'spaces'
op|'='
string|"' '"
op|'*'
op|'('
name|'len'
op|'('
name|'last'
op|')'
op|'-'
name|'len'
op|'('
name|'last'
op|'.'
name|'lstrip'
op|'('
op|')'
op|')'
op|'-'
number|'1'
op|')'
newline|'\n'
name|'buffer'
op|'['
op|'-'
number|'1'
op|']'
op|'='
name|'spaces'
op|'+'
op|'('
string|"' * @deprecated (%s) Use latest\\n'"
op|'%'
name|'new_version'
op|')'
newline|'\n'
name|'buffer'
op|'.'
name|'append'
op|'('
name|'spaces'
op|'+'
string|"' */\\n'"
op|')'
newline|'\n'
name|'buffer'
op|'.'
name|'append'
op|'('
name|'spaces'
op|'+'
string|"'@Deprecated\\n'"
op|')'
newline|'\n'
nl|'\n'
DECL|function|buffer_constant
dedent|''
dedent|''
name|'def'
name|'buffer_constant'
op|'('
name|'buffer'
op|','
name|'line'
op|')'
op|':'
newline|'\n'
indent|'    '
name|'spaces'
op|'='
string|"' '"
op|'*'
op|'('
name|'len'
op|'('
name|'line'
op|')'
op|'-'
name|'len'
op|'('
name|'line'
op|'.'
name|'lstrip'
op|'('
op|')'
op|')'
op|')'
newline|'\n'
name|'buffer'
op|'.'
name|'append'
op|'('
string|"'\\n'"
op|'+'
name|'spaces'
op|'+'
string|"'/**\\n'"
op|')'
newline|'\n'
name|'buffer'
op|'.'
name|'append'
op|'('
name|'spaces'
op|'+'
string|"' * Match settings and bugs in Lucene\\'s %s release.\\n'"
op|'%'
name|'new_version'
op|')'
newline|'\n'
name|'if'
name|'deprecate'
op|':'
newline|'\n'
indent|'      '
name|'buffer'
op|'.'
name|'append'
op|'('
name|'spaces'
op|'+'
string|"' * @deprecated Use latest\\n'"
op|')'
newline|'\n'
dedent|''
name|'buffer'
op|'.'
name|'append'
op|'('
name|'spaces'
op|'+'
string|"' */\\n'"
op|')'
newline|'\n'
name|'if'
name|'deprecate'
op|':'
newline|'\n'
indent|'      '
name|'buffer'
op|'.'
name|'append'
op|'('
name|'spaces'
op|'+'
string|"'@Deprecated\\n'"
op|')'
newline|'\n'
dedent|''
name|'buffer'
op|'.'
name|'append'
op|'('
name|'spaces'
op|'+'
string|"'public static final Version %s = new Version(%d, %d, %d);\\n'"
op|'%'
nl|'\n'
op|'('
name|'new_version'
op|'.'
name|'constant'
op|','
name|'new_version'
op|'.'
name|'major'
op|','
name|'new_version'
op|'.'
name|'minor'
op|','
name|'new_version'
op|'.'
name|'bugfix'
op|')'
op|')'
newline|'\n'
nl|'\n'
DECL|class|Edit
dedent|''
name|'class'
name|'Edit'
op|'('
name|'object'
op|')'
op|':'
newline|'\n'
DECL|variable|found
indent|'    '
name|'found'
op|'='
op|'-'
number|'1'
newline|'\n'
DECL|member|__call__
name|'def'
name|'__call__'
op|'('
name|'self'
op|','
name|'buffer'
op|','
name|'match'
op|','
name|'line'
op|')'
op|':'
newline|'\n'
indent|'      '
name|'if'
name|'new_version'
op|'.'
name|'constant'
name|'in'
name|'line'
op|':'
newline|'\n'
indent|'        '
name|'return'
name|'None'
comment|'# constant already exists'
newline|'\n'
comment|'# outter match is just to find lines declaring version constants'
nl|'\n'
dedent|''
name|'match'
op|'='
name|'prev_matcher'
op|'.'
name|'search'
op|'('
name|'line'
op|')'
newline|'\n'
name|'if'
name|'match'
name|'is'
name|'not'
name|'None'
op|':'
newline|'\n'
indent|'        '
name|'ensure_deprecated'
op|'('
name|'buffer'
op|')'
comment|'# old version should be deprecated'
newline|'\n'
name|'self'
op|'.'
name|'found'
op|'='
name|'len'
op|'('
name|'buffer'
op|')'
op|'+'
number|'1'
comment|'# extra 1 for buffering current line below'
newline|'\n'
dedent|''
name|'elif'
name|'self'
op|'.'
name|'found'
op|'!='
op|'-'
number|'1'
op|':'
newline|'\n'
comment|"# we didn't match, but we previously had a match, so insert new version here"
nl|'\n'
comment|'# first find where to insert (first empty line before current constant)'
nl|'\n'
indent|'        '
name|'c'
op|'='
op|'['
op|']'
newline|'\n'
name|'buffer_constant'
op|'('
name|'c'
op|','
name|'line'
op|')'
newline|'\n'
name|'tmp'
op|'='
name|'buffer'
op|'['
name|'self'
op|'.'
name|'found'
op|':'
op|']'
newline|'\n'
name|'buffer'
op|'['
name|'self'
op|'.'
name|'found'
op|':'
op|']'
op|'='
name|'c'
newline|'\n'
name|'buffer'
op|'.'
name|'extend'
op|'('
name|'tmp'
op|')'
newline|'\n'
name|'buffer'
op|'.'
name|'append'
op|'('
name|'line'
op|')'
newline|'\n'
name|'return'
name|'True'
newline|'\n'
nl|'\n'
dedent|''
name|'buffer'
op|'.'
name|'append'
op|'('
name|'line'
op|')'
newline|'\n'
name|'return'
name|'False'
newline|'\n'
nl|'\n'
dedent|''
dedent|''
name|'changed'
op|'='
name|'update_file'
op|'('
name|'filename'
op|','
name|'matcher'
op|','
name|'Edit'
op|'('
op|')'
op|')'
newline|'\n'
name|'print'
op|'('
string|"'done'"
name|'if'
name|'changed'
name|'else'
string|"'uptodate'"
op|')'
newline|'\n'
nl|'\n'
DECL|variable|version_prop_re
dedent|''
name|'version_prop_re'
op|'='
name|'re'
op|'.'
name|'compile'
op|'('
string|"'version\\.base=(.*)'"
op|')'
newline|'\n'
DECL|function|update_build_version
name|'def'
name|'update_build_version'
op|'('
name|'new_version'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'print'
op|'('
string|"'  changing version.base...'"
op|','
name|'end'
op|'='
string|"''"
op|')'
newline|'\n'
name|'filename'
op|'='
string|"'lucene/version.properties'"
newline|'\n'
DECL|function|edit
name|'def'
name|'edit'
op|'('
name|'buffer'
op|','
name|'match'
op|','
name|'line'
op|')'
op|':'
newline|'\n'
indent|'    '
name|'if'
name|'new_version'
op|'.'
name|'dot'
name|'in'
name|'line'
op|':'
newline|'\n'
indent|'      '
name|'return'
name|'None'
newline|'\n'
dedent|''
name|'buffer'
op|'.'
name|'append'
op|'('
string|"'version.base='"
op|'+'
name|'new_version'
op|'.'
name|'dot'
op|'+'
string|"'\\n'"
op|')'
newline|'\n'
name|'return'
name|'True'
newline|'\n'
nl|'\n'
dedent|''
name|'changed'
op|'='
name|'update_file'
op|'('
name|'filename'
op|','
name|'version_prop_re'
op|','
name|'edit'
op|')'
newline|'\n'
name|'print'
op|'('
string|"'done'"
name|'if'
name|'changed'
name|'else'
string|"'uptodate'"
op|')'
newline|'\n'
nl|'\n'
DECL|function|update_latest_constant
dedent|''
name|'def'
name|'update_latest_constant'
op|'('
name|'new_version'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'print'
op|'('
string|"'  changing Version.LATEST to %s...'"
op|'%'
name|'new_version'
op|'.'
name|'constant'
op|','
name|'end'
op|'='
string|"''"
op|')'
newline|'\n'
name|'filename'
op|'='
string|"'lucene/core/src/java/org/apache/lucene/util/Version.java'"
newline|'\n'
name|'matcher'
op|'='
name|'re'
op|'.'
name|'compile'
op|'('
string|"'public static final Version LATEST'"
op|')'
newline|'\n'
DECL|function|edit
name|'def'
name|'edit'
op|'('
name|'buffer'
op|','
name|'match'
op|','
name|'line'
op|')'
op|':'
newline|'\n'
indent|'    '
name|'if'
name|'new_version'
op|'.'
name|'constant'
name|'in'
name|'line'
op|':'
newline|'\n'
indent|'      '
name|'return'
name|'None'
newline|'\n'
dedent|''
name|'buffer'
op|'.'
name|'append'
op|'('
name|'line'
op|'.'
name|'rpartition'
op|'('
string|"'='"
op|')'
op|'['
number|'0'
op|']'
op|'+'
op|'('
string|"'= %s;\\n'"
op|'%'
name|'new_version'
op|'.'
name|'constant'
op|')'
op|')'
newline|'\n'
name|'return'
name|'True'
newline|'\n'
nl|'\n'
dedent|''
name|'changed'
op|'='
name|'update_file'
op|'('
name|'filename'
op|','
name|'matcher'
op|','
name|'edit'
op|')'
newline|'\n'
name|'print'
op|'('
string|"'done'"
name|'if'
name|'changed'
name|'else'
string|"'uptodate'"
op|')'
newline|'\n'
nl|'\n'
DECL|function|update_example_solrconfigs
dedent|''
name|'def'
name|'update_example_solrconfigs'
op|'('
name|'new_version'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'print'
op|'('
string|"'  updating example solrconfig.xml files'"
op|')'
newline|'\n'
name|'matcher'
op|'='
name|'re'
op|'.'
name|'compile'
op|'('
string|"'<luceneMatchVersion>'"
op|')'
newline|'\n'
nl|'\n'
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
string|"'solr/example'"
op|')'
op|':'
newline|'\n'
indent|'    '
name|'for'
name|'f'
name|'in'
name|'files'
op|':'
newline|'\n'
indent|'      '
name|'if'
name|'f'
op|'=='
string|"'solrconfig.xml'"
op|':'
newline|'\n'
indent|'        '
name|'update_solrconfig'
op|'('
name|'os'
op|'.'
name|'path'
op|'.'
name|'join'
op|'('
name|'root'
op|','
name|'f'
op|')'
op|','
name|'matcher'
op|','
name|'new_version'
op|')'
newline|'\n'
nl|'\n'
DECL|function|update_solrconfig
dedent|''
dedent|''
dedent|''
dedent|''
name|'def'
name|'update_solrconfig'
op|'('
name|'filename'
op|','
name|'matcher'
op|','
name|'new_version'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'print'
op|'('
string|"'    %s...'"
op|'%'
name|'filename'
op|','
name|'end'
op|'='
string|"''"
op|')'
newline|'\n'
DECL|function|edit
name|'def'
name|'edit'
op|'('
name|'buffer'
op|','
name|'match'
op|','
name|'line'
op|')'
op|':'
newline|'\n'
indent|'    '
name|'if'
name|'new_version'
op|'.'
name|'dot'
name|'in'
name|'line'
op|':'
newline|'\n'
indent|'      '
name|'return'
name|'None'
newline|'\n'
dedent|''
name|'match'
op|'='
name|'new_version'
op|'.'
name|'previous_dot_matcher'
op|'.'
name|'search'
op|'('
name|'line'
op|')'
newline|'\n'
name|'if'
name|'match'
name|'is'
name|'None'
op|':'
newline|'\n'
indent|'      '
name|'return'
name|'False'
newline|'\n'
dedent|''
name|'buffer'
op|'.'
name|'append'
op|'('
name|'line'
op|'.'
name|'replace'
op|'('
name|'match'
op|'.'
name|'group'
op|'('
number|'1'
op|')'
op|','
name|'new_version'
op|'.'
name|'dot'
op|')'
op|')'
newline|'\n'
name|'return'
name|'True'
newline|'\n'
nl|'\n'
dedent|''
name|'changed'
op|'='
name|'update_file'
op|'('
name|'filename'
op|','
name|'matcher'
op|','
name|'edit'
op|')'
newline|'\n'
name|'print'
op|'('
string|"'done'"
name|'if'
name|'changed'
name|'else'
string|"'uptodate'"
op|')'
newline|'\n'
nl|'\n'
DECL|function|codec_exists
dedent|''
name|'def'
name|'codec_exists'
op|'('
name|'version'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'codecs_dir'
op|'='
string|"'lucene/core/src/java/org/apache/lucene/codecs'"
newline|'\n'
name|'codec_file'
op|'='
string|"'%(dir)s/lucene%(x)s%(y)s/Lucene%(x)s%(y)sCodec.java'"
newline|'\n'
name|'return'
name|'os'
op|'.'
name|'path'
op|'.'
name|'exists'
op|'('
name|'codec_file'
op|'%'
op|'{'
string|"'x'"
op|':'
name|'version'
op|'.'
name|'major'
op|','
string|"'y'"
op|':'
name|'version'
op|'.'
name|'minor'
op|','
string|"'dir'"
op|':'
name|'codecs_dir'
op|'}'
op|')'
newline|'\n'
nl|'\n'
DECL|function|create_backcompat_indexes
dedent|''
name|'def'
name|'create_backcompat_indexes'
op|'('
name|'version'
op|','
name|'on_trunk'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'majorminor'
op|'='
string|"'%d%d'"
op|'%'
op|'('
name|'version'
op|'.'
name|'major'
op|','
name|'version'
op|'.'
name|'minor'
op|')'
newline|'\n'
name|'codec'
op|'='
string|"'Lucene%s'"
op|'%'
name|'majorminor'
newline|'\n'
name|'backcompat_dir'
op|'='
string|"'lucene/backward-codecs'"
name|'if'
name|'on_trunk'
name|'else'
string|"'lucene/core'"
newline|'\n'
nl|'\n'
name|'create_index'
op|'('
name|'codec'
op|','
name|'backcompat_dir'
op|','
string|"'cfs'"
op|','
name|'majorminor'
op|')'
newline|'\n'
name|'create_index'
op|'('
name|'codec'
op|','
name|'backcompat_dir'
op|','
string|"'nocfs'"
op|','
name|'majorminor'
op|')'
newline|'\n'
nl|'\n'
DECL|function|create_index
dedent|''
name|'def'
name|'create_index'
op|'('
name|'codec'
op|','
name|'codecs_dir'
op|','
name|'type'
op|','
name|'majorminor'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'filename'
op|'='
string|"'index.%s.%s.zip'"
op|'%'
op|'('
name|'majorminor'
op|','
name|'type'
op|')'
newline|'\n'
name|'print'
op|'('
string|"'  creating %s...'"
op|'%'
name|'filename'
op|','
name|'end'
op|'='
string|"''"
op|')'
newline|'\n'
name|'index_dir'
op|'='
string|"'src/test/org/apache/lucene/index'"
newline|'\n'
name|'if'
name|'os'
op|'.'
name|'path'
op|'.'
name|'exists'
op|'('
name|'os'
op|'.'
name|'path'
op|'.'
name|'join'
op|'('
name|'codecs_dir'
op|','
name|'index_dir'
op|','
name|'filename'
op|')'
op|')'
op|':'
newline|'\n'
indent|'    '
name|'print'
op|'('
string|"'uptodate'"
op|')'
newline|'\n'
name|'return'
newline|'\n'
nl|'\n'
dedent|''
name|'test'
op|'='
op|'{'
string|"'cfs'"
op|':'
string|"'testCreateCFS'"
op|','
string|"'nocfs'"
op|':'
string|"'testCreateNonCFS'"
op|'}'
op|'['
name|'type'
op|']'
newline|'\n'
name|'ant_args'
op|'='
string|"' '"
op|'.'
name|'join'
op|'('
op|'['
nl|'\n'
string|"'-Dtests.codec=%s'"
op|'%'
name|'codec'
op|','
nl|'\n'
string|"'-Dtests.useSecurityManager=false'"
op|','
nl|'\n'
string|"'-Dtestcase=CreateBackwardsCompatibilityIndex'"
op|','
nl|'\n'
string|"'-Dtestmethod=%s'"
op|'%'
name|'test'
nl|'\n'
op|']'
op|')'
newline|'\n'
name|'base_dir'
op|'='
name|'os'
op|'.'
name|'getcwd'
op|'('
op|')'
newline|'\n'
name|'bc_index_dir'
op|'='
string|"'/tmp/idx/index.%s'"
op|'%'
name|'type'
newline|'\n'
name|'bc_index_file'
op|'='
name|'os'
op|'.'
name|'path'
op|'.'
name|'join'
op|'('
name|'bc_index_dir'
op|','
name|'filename'
op|')'
newline|'\n'
nl|'\n'
name|'success'
op|'='
name|'False'
newline|'\n'
name|'if'
name|'not'
name|'os'
op|'.'
name|'path'
op|'.'
name|'exists'
op|'('
name|'bc_index_file'
op|')'
op|':'
newline|'\n'
indent|'    '
name|'os'
op|'.'
name|'chdir'
op|'('
name|'codecs_dir'
op|')'
newline|'\n'
name|'run'
op|'('
string|"'ant test %s'"
op|'%'
name|'ant_args'
op|')'
newline|'\n'
name|'os'
op|'.'
name|'chdir'
op|'('
string|"'/tmp/idx/index.%s'"
op|'%'
name|'type'
op|')'
newline|'\n'
name|'run'
op|'('
string|"'zip %s *'"
op|'%'
name|'filename'
op|')'
newline|'\n'
dedent|''
name|'run'
op|'('
string|"'cp %s %s'"
op|'%'
op|'('
name|'bc_index_file'
op|','
name|'os'
op|'.'
name|'path'
op|'.'
name|'join'
op|'('
name|'base_dir'
op|','
name|'codecs_dir'
op|','
name|'index_dir'
op|')'
op|')'
op|')'
newline|'\n'
name|'os'
op|'.'
name|'chdir'
op|'('
name|'base_dir'
op|')'
newline|'\n'
name|'run'
op|'('
string|"'svn add %s'"
op|'%'
name|'os'
op|'.'
name|'path'
op|'.'
name|'join'
op|'('
name|'codecs_dir'
op|','
name|'index_dir'
op|','
name|'filename'
op|')'
op|')'
newline|'\n'
name|'success'
op|'='
name|'True'
newline|'\n'
nl|'\n'
name|'os'
op|'.'
name|'chdir'
op|'('
name|'base_dir'
op|')'
newline|'\n'
name|'run'
op|'('
string|"'rm -rf %s'"
op|'%'
name|'bc_index_dir'
op|')'
newline|'\n'
name|'if'
name|'success'
op|':'
newline|'\n'
indent|'    '
name|'print'
op|'('
string|"'done'"
op|')'
newline|'\n'
nl|'\n'
DECL|function|update_backcompat_tests
dedent|''
dedent|''
name|'def'
name|'update_backcompat_tests'
op|'('
name|'version'
op|','
name|'on_trunk'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'majorminor'
op|'='
string|"'%d%d'"
op|'%'
op|'('
name|'version'
op|'.'
name|'major'
op|','
name|'version'
op|'.'
name|'minor'
op|')'
newline|'\n'
name|'print'
op|'('
string|"'  adding new indexes to backcompat tests...'"
op|','
name|'end'
op|'='
string|"''"
op|')'
newline|'\n'
name|'basedir'
op|'='
string|"'lucene/backward-codecs'"
name|'if'
name|'on_trunk'
name|'else'
string|"'lucene/core'"
newline|'\n'
name|'filename'
op|'='
string|"'%s/src/test/org/apache/lucene/index/TestBackwardsCompatibility.java'"
op|'%'
name|'basedir'
newline|'\n'
name|'matcher'
op|'='
name|'re'
op|'.'
name|'compile'
op|'('
string|"r'final static String\\[\\] oldNames = {|};'"
op|')'
newline|'\n'
name|'cfs_name'
op|'='
string|"'%s.cfs'"
op|'%'
name|'majorminor'
newline|'\n'
name|'nocfs_name'
op|'='
string|"'%s.nocfs'"
op|'%'
name|'majorminor'
newline|'\n'
nl|'\n'
DECL|class|Edit
name|'class'
name|'Edit'
op|'('
name|'object'
op|')'
op|':'
newline|'\n'
DECL|variable|start
indent|'    '
name|'start'
op|'='
name|'None'
newline|'\n'
DECL|member|__call__
name|'def'
name|'__call__'
op|'('
name|'self'
op|','
name|'buffer'
op|','
name|'match'
op|','
name|'line'
op|')'
op|':'
newline|'\n'
indent|'      '
name|'if'
name|'self'
op|'.'
name|'start'
op|':'
newline|'\n'
comment|'# first check if the indexes we are adding already exist      '
nl|'\n'
indent|'        '
name|'last_ndx'
op|'='
name|'len'
op|'('
name|'buffer'
op|')'
op|'-'
number|'1'
newline|'\n'
name|'i'
op|'='
name|'last_ndx'
newline|'\n'
name|'while'
name|'i'
op|'>='
name|'self'
op|'.'
name|'start'
op|':'
newline|'\n'
indent|'          '
name|'if'
name|'cfs_name'
name|'in'
name|'buffer'
op|'['
name|'i'
op|']'
op|':'
newline|'\n'
indent|'            '
name|'return'
name|'None'
newline|'\n'
dedent|''
name|'i'
op|'-='
number|'1'
newline|'\n'
nl|'\n'
dedent|''
name|'last'
op|'='
name|'buffer'
op|'['
name|'last_ndx'
op|']'
newline|'\n'
name|'spaces'
op|'='
string|"' '"
op|'*'
op|'('
name|'len'
op|'('
name|'last'
op|')'
op|'-'
name|'len'
op|'('
name|'last'
op|'.'
name|'lstrip'
op|'('
op|')'
op|')'
op|')'
newline|'\n'
name|'quote_ndx'
op|'='
name|'last'
op|'.'
name|'find'
op|'('
string|'\'"\''
op|')'
newline|'\n'
name|'quote_ndx'
op|'='
name|'last'
op|'.'
name|'find'
op|'('
string|'\'"\''
op|','
name|'quote_ndx'
op|'+'
number|'1'
op|')'
newline|'\n'
name|'buffer'
op|'['
name|'last_ndx'
op|']'
op|'='
name|'last'
op|'['
op|':'
name|'quote_ndx'
op|'+'
number|'1'
op|']'
op|'+'
string|'","'
op|'+'
name|'last'
op|'['
name|'quote_ndx'
op|'+'
number|'1'
op|':'
op|']'
newline|'\n'
name|'buffer'
op|'.'
name|'append'
op|'('
name|'spaces'
op|'+'
op|'('
string|'\'"%s",\\n\''
op|'%'
name|'cfs_name'
op|')'
op|')'
newline|'\n'
name|'buffer'
op|'.'
name|'append'
op|'('
name|'spaces'
op|'+'
op|'('
string|'\'"%s"\\n\''
op|'%'
name|'nocfs_name'
op|')'
op|')'
newline|'\n'
name|'buffer'
op|'.'
name|'append'
op|'('
name|'line'
op|')'
newline|'\n'
name|'return'
name|'True'
newline|'\n'
nl|'\n'
dedent|''
name|'if'
string|"'oldNames'"
name|'in'
name|'line'
op|':'
newline|'\n'
indent|'        '
name|'self'
op|'.'
name|'start'
op|'='
name|'len'
op|'('
name|'buffer'
op|')'
comment|'# location of first index name'
newline|'\n'
dedent|''
name|'buffer'
op|'.'
name|'append'
op|'('
name|'line'
op|')'
newline|'\n'
name|'return'
name|'False'
newline|'\n'
nl|'\n'
dedent|''
dedent|''
name|'changed'
op|'='
name|'update_file'
op|'('
name|'filename'
op|','
name|'matcher'
op|','
name|'Edit'
op|'('
op|')'
op|')'
newline|'\n'
name|'print'
op|'('
string|"'done'"
name|'if'
name|'changed'
name|'else'
string|"'uptodate'"
op|')'
newline|'\n'
nl|'\n'
DECL|function|check_lucene_version_tests
dedent|''
name|'def'
name|'check_lucene_version_tests'
op|'('
op|')'
op|':'
newline|'\n'
indent|'  '
name|'print'
op|'('
string|"'  checking lucene version tests...'"
op|','
name|'end'
op|'='
string|"''"
op|')'
newline|'\n'
name|'base_dir'
op|'='
name|'os'
op|'.'
name|'getcwd'
op|'('
op|')'
newline|'\n'
name|'os'
op|'.'
name|'chdir'
op|'('
string|"'lucene/core'"
op|')'
newline|'\n'
name|'run'
op|'('
string|"'ant test -Dtestcase=TestVersion'"
op|')'
newline|'\n'
name|'os'
op|'.'
name|'chdir'
op|'('
name|'base_dir'
op|')'
newline|'\n'
name|'print'
op|'('
string|"'ok'"
op|')'
newline|'\n'
nl|'\n'
DECL|function|check_solr_version_tests
dedent|''
name|'def'
name|'check_solr_version_tests'
op|'('
op|')'
op|':'
newline|'\n'
indent|'  '
name|'print'
op|'('
string|"'  checking solr version tests...'"
op|','
name|'end'
op|'='
string|"''"
op|')'
newline|'\n'
name|'base_dir'
op|'='
name|'os'
op|'.'
name|'getcwd'
op|'('
op|')'
newline|'\n'
name|'os'
op|'.'
name|'chdir'
op|'('
string|"'solr/core'"
op|')'
newline|'\n'
name|'run'
op|'('
string|"'ant test -Dtestcase=TestLuceneMatchVersion'"
op|')'
newline|'\n'
name|'os'
op|'.'
name|'chdir'
op|'('
name|'base_dir'
op|')'
newline|'\n'
name|'print'
op|'('
string|"'ok'"
op|')'
newline|'\n'
nl|'\n'
DECL|function|check_backcompat_tests
dedent|''
name|'def'
name|'check_backcompat_tests'
op|'('
name|'on_trunk'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'print'
op|'('
string|"'  checking backcompat tests...'"
op|','
name|'end'
op|'='
string|"''"
op|')'
newline|'\n'
name|'base_dir'
op|'='
name|'os'
op|'.'
name|'getcwd'
op|'('
op|')'
newline|'\n'
name|'basedir'
op|'='
string|"'lucene/backward-codecs'"
name|'if'
name|'on_trunk'
name|'else'
string|"'lucene/core'"
newline|'\n'
name|'os'
op|'.'
name|'chdir'
op|'('
name|'basedir'
op|')'
newline|'\n'
name|'run'
op|'('
string|"'ant test -Dtestcase=TestBackwardsCompatibility'"
op|')'
newline|'\n'
name|'os'
op|'.'
name|'chdir'
op|'('
name|'base_dir'
op|')'
newline|'\n'
name|'print'
op|'('
string|"'ok'"
op|')'
newline|'\n'
nl|'\n'
comment|'# branch types are "release", "stable" and "trunk"'
nl|'\n'
DECL|function|find_branch_type
dedent|''
name|'def'
name|'find_branch_type'
op|'('
op|')'
op|':'
newline|'\n'
indent|'  '
name|'output'
op|'='
name|'subprocess'
op|'.'
name|'check_output'
op|'('
string|"'svn info'"
op|','
name|'shell'
op|'='
name|'True'
op|')'
newline|'\n'
name|'for'
name|'line'
name|'in'
name|'output'
op|'.'
name|'split'
op|'('
string|"b'\\n'"
op|')'
op|':'
newline|'\n'
indent|'    '
name|'if'
name|'line'
op|'.'
name|'startswith'
op|'('
string|"b'URL:'"
op|')'
op|':'
newline|'\n'
indent|'      '
name|'url'
op|'='
name|'line'
op|'.'
name|'split'
op|'('
string|"b'/'"
op|')'
op|'['
op|'-'
number|'1'
op|']'
newline|'\n'
name|'break'
newline|'\n'
dedent|''
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'    '
name|'raise'
name|'Exception'
op|'('
string|"'svn info missing repo URL'"
op|')'
newline|'\n'
nl|'\n'
dedent|''
name|'if'
name|'url'
op|'=='
string|"b'trunk'"
op|':'
newline|'\n'
indent|'    '
name|'return'
string|"'trunk'"
newline|'\n'
dedent|''
name|'if'
name|'url'
op|'.'
name|'startswith'
op|'('
string|"b'branch_'"
op|')'
op|':'
newline|'\n'
indent|'    '
name|'return'
string|"'stable'"
newline|'\n'
dedent|''
name|'if'
name|'url'
op|'.'
name|'startswith'
op|'('
string|"b'lucene_solr_'"
op|')'
op|':'
newline|'\n'
indent|'    '
name|'return'
string|"'release'"
newline|'\n'
dedent|''
name|'raise'
name|'Exception'
op|'('
string|"'Cannot run bumpVersion.py on feature branch'"
op|')'
newline|'\n'
nl|'\n'
DECL|function|find_previous_version
dedent|''
name|'def'
name|'find_previous_version'
op|'('
op|')'
op|':'
newline|'\n'
indent|'  '
name|'return'
name|'version_prop_re'
op|'.'
name|'search'
op|'('
name|'open'
op|'('
string|"'lucene/version.properties'"
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
DECL|function|merge_change
dedent|''
name|'def'
name|'merge_change'
op|'('
name|'changeid'
op|','
name|'repo'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'print'
op|'('
string|"'\\nMerging downstream change %d...'"
op|'%'
name|'changeid'
op|','
name|'end'
op|'='
string|"''"
op|')'
newline|'\n'
name|'run'
op|'('
string|"'svn merge -c %d --record-only %s'"
op|'%'
op|'('
name|'changeid'
op|','
name|'repo'
op|')'
op|')'
newline|'\n'
name|'print'
op|'('
string|"'done'"
op|')'
newline|'\n'
nl|'\n'
DECL|function|read_config
dedent|''
name|'def'
name|'read_config'
op|'('
op|')'
op|':'
newline|'\n'
indent|'  '
name|'parser'
op|'='
name|'argparse'
op|'.'
name|'ArgumentParser'
op|'('
name|'description'
op|'='
string|"'Add a new version'"
op|')'
newline|'\n'
name|'parser'
op|'.'
name|'add_argument'
op|'('
string|"'version'"
op|','
name|'type'
op|'='
name|'Version'
op|'.'
name|'parse'
op|')'
newline|'\n'
name|'parser'
op|'.'
name|'add_argument'
op|'('
string|"'-c'"
op|','
string|"'--changeid'"
op|','
name|'type'
op|'='
name|'int'
op|','
name|'help'
op|'='
string|"'SVN ChangeId for downstream version change to merge'"
op|')'
newline|'\n'
name|'parser'
op|'.'
name|'add_argument'
op|'('
string|"'-r'"
op|','
string|"'--downstream-repo'"
op|','
name|'help'
op|'='
string|"'Path to downstream checkout for given changeid'"
op|')'
newline|'\n'
name|'c'
op|'='
name|'parser'
op|'.'
name|'parse_args'
op|'('
op|')'
newline|'\n'
nl|'\n'
name|'c'
op|'.'
name|'branch_type'
op|'='
name|'find_branch_type'
op|'('
op|')'
newline|'\n'
name|'c'
op|'.'
name|'matching_branch'
op|'='
name|'c'
op|'.'
name|'version'
op|'.'
name|'is_bugfix_release'
op|'('
op|')'
name|'and'
name|'c'
op|'.'
name|'branch_type'
op|'=='
string|"'release'"
name|'or'
name|'c'
op|'.'
name|'version'
op|'.'
name|'is_minor_release'
op|'('
op|')'
name|'and'
name|'c'
op|'.'
name|'branch_type'
op|'=='
string|"'stable'"
name|'or'
name|'c'
op|'.'
name|'branch_type'
op|'=='
string|"'major'"
newline|'\n'
nl|'\n'
name|'if'
name|'c'
op|'.'
name|'matching_branch'
op|':'
newline|'\n'
indent|'    '
name|'c'
op|'.'
name|'previous_version'
op|'='
name|'Version'
op|'.'
name|'parse'
op|'('
name|'find_previous_version'
op|'('
op|')'
op|')'
newline|'\n'
dedent|''
name|'elif'
name|'c'
op|'.'
name|'version'
op|'.'
name|'is_minor_release'
op|'('
op|')'
op|':'
newline|'\n'
indent|'    '
name|'c'
op|'.'
name|'previous_version'
op|'='
name|'Version'
op|'('
name|'c'
op|'.'
name|'version'
op|'.'
name|'major'
op|','
name|'c'
op|'.'
name|'version'
op|'.'
name|'minor'
op|'-'
number|'1'
op|','
number|'0'
op|')'
newline|'\n'
dedent|''
name|'elif'
name|'c'
op|'.'
name|'version'
op|'.'
name|'is_bugfix_release'
op|'('
op|')'
op|':'
newline|'\n'
indent|'    '
name|'c'
op|'.'
name|'previous_version'
op|'='
name|'Version'
op|'('
name|'c'
op|'.'
name|'version'
op|'.'
name|'major'
op|','
name|'c'
op|'.'
name|'version'
op|'.'
name|'minor'
op|','
name|'c'
op|'.'
name|'version'
op|'.'
name|'bugfix'
op|'-'
number|'1'
op|')'
newline|'\n'
nl|'\n'
dedent|''
name|'if'
name|'bool'
op|'('
name|'c'
op|'.'
name|'changeid'
op|')'
op|'!='
name|'bool'
op|'('
name|'c'
op|'.'
name|'downstream_repo'
op|')'
op|':'
newline|'\n'
indent|'    '
name|'parser'
op|'.'
name|'error'
op|'('
string|"'--changeid and --upstream-repo must be used together'"
op|')'
newline|'\n'
dedent|''
name|'if'
name|'not'
name|'c'
op|'.'
name|'changeid'
name|'and'
name|'not'
name|'c'
op|'.'
name|'matching_branch'
op|':'
newline|'\n'
indent|'    '
name|'parser'
op|'.'
name|'error'
op|'('
string|"'Must use --changeid for forward porting bugfix release version to other branches'"
op|')'
newline|'\n'
dedent|''
name|'if'
name|'c'
op|'.'
name|'changeid'
name|'and'
name|'c'
op|'.'
name|'matching_branch'
op|':'
newline|'\n'
indent|'    '
name|'parser'
op|'.'
name|'error'
op|'('
string|"'Cannot use --changeid on branch that new version will originate on'"
op|')'
newline|'\n'
dedent|''
name|'if'
name|'c'
op|'.'
name|'changeid'
name|'and'
name|'c'
op|'.'
name|'version'
op|'.'
name|'is_major_release'
op|'('
op|')'
op|':'
newline|'\n'
indent|'    '
name|'parser'
op|'.'
name|'error'
op|'('
string|"'Cannot use --changeid for major release'"
op|')'
newline|'\n'
nl|'\n'
dedent|''
name|'return'
name|'c'
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
name|'read_config'
op|'('
op|')'
newline|'\n'
nl|'\n'
name|'if'
name|'c'
op|'.'
name|'changeid'
op|':'
newline|'\n'
indent|'    '
name|'merge_change'
op|'('
name|'c'
op|'.'
name|'changeid'
op|','
name|'c'
op|'.'
name|'downstream_repo'
op|')'
newline|'\n'
nl|'\n'
dedent|''
name|'print'
op|'('
string|"'\\nAdding new version %s'"
op|'%'
name|'c'
op|'.'
name|'version'
op|')'
newline|'\n'
name|'update_changes'
op|'('
string|"'lucene/CHANGES.txt'"
op|','
name|'c'
op|'.'
name|'version'
op|')'
newline|'\n'
name|'update_changes'
op|'('
string|"'solr/CHANGES.txt'"
op|','
name|'c'
op|'.'
name|'version'
op|')'
newline|'\n'
name|'add_constant'
op|'('
name|'c'
op|'.'
name|'version'
op|','
name|'not'
name|'c'
op|'.'
name|'matching_branch'
op|')'
newline|'\n'
nl|'\n'
name|'if'
name|'not'
name|'c'
op|'.'
name|'changeid'
op|':'
newline|'\n'
indent|'    '
name|'print'
op|'('
string|"'\\nUpdating latest version'"
op|')'
newline|'\n'
name|'update_build_version'
op|'('
name|'c'
op|'.'
name|'version'
op|')'
newline|'\n'
name|'update_latest_constant'
op|'('
name|'c'
op|'.'
name|'version'
op|')'
newline|'\n'
name|'update_example_solrconfigs'
op|'('
name|'c'
op|'.'
name|'version'
op|')'
newline|'\n'
nl|'\n'
dedent|''
name|'run_backcompat_tests'
op|'='
name|'False'
newline|'\n'
name|'on_trunk'
op|'='
name|'c'
op|'.'
name|'branch_type'
op|'=='
string|"'trunk'"
newline|'\n'
name|'if'
name|'not'
name|'c'
op|'.'
name|'version'
op|'.'
name|'is_bugfix_release'
op|'('
op|')'
name|'and'
name|'codec_exists'
op|'('
name|'c'
op|'.'
name|'previous_version'
op|')'
op|':'
newline|'\n'
indent|'    '
name|'print'
op|'('
string|"'\\nCreating backwards compatibility tests'"
op|')'
newline|'\n'
name|'create_backcompat_indexes'
op|'('
name|'c'
op|'.'
name|'previous_version'
op|','
name|'on_trunk'
op|')'
newline|'\n'
name|'update_backcompat_tests'
op|'('
name|'c'
op|'.'
name|'previous_version'
op|','
name|'on_trunk'
op|')'
newline|'\n'
name|'run_backcompat_tests'
op|'='
name|'True'
newline|'\n'
nl|'\n'
dedent|''
name|'if'
name|'c'
op|'.'
name|'version'
op|'.'
name|'is_major_release'
op|'('
op|')'
op|':'
newline|'\n'
indent|'    '
name|'print'
op|'('
string|"'\\nTODO: '"
op|')'
newline|'\n'
name|'print'
op|'('
string|"'  - Update major version bounds in Version.java'"
op|')'
newline|'\n'
name|'print'
op|'('
string|"'  - Move backcompat oldIndexes to unsupportedIndexes in TestBackwardsCompatibility'"
op|')'
newline|'\n'
name|'print'
op|'('
string|"'  - Update IndexFormatTooOldException throw cases'"
op|')'
newline|'\n'
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'    '
name|'print'
op|'('
string|"'\\nTesting changes'"
op|')'
newline|'\n'
name|'check_lucene_version_tests'
op|'('
op|')'
newline|'\n'
name|'check_solr_version_tests'
op|'('
op|')'
newline|'\n'
name|'if'
name|'run_backcompat_tests'
op|':'
newline|'\n'
indent|'      '
name|'check_backcompat_tests'
op|'('
name|'on_trunk'
op|')'
newline|'\n'
nl|'\n'
dedent|''
dedent|''
name|'print'
op|'('
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
string|"'\\nReceived Ctrl-C, exiting early'"
op|')'
newline|'\n'
dedent|''
dedent|''
endmarker|''
end_unit
