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
name|'re'
newline|'\n'
name|'import'
name|'subprocess'
newline|'\n'
name|'import'
name|'sys'
newline|'\n'
name|'from'
name|'enum'
name|'import'
name|'Enum'
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
op|','
name|'prerelease'
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
name|'prerelease'
op|'='
name|'prerelease'
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
string|"r'(\\d+)\\.(\\d+).(\\d+)(.1|.2)?'"
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
string|"'Version argument must be of format x.y.z(.1|.2)?'"
op|')'
newline|'\n'
dedent|''
name|'parts'
op|'='
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
op|'['
op|':'
op|'-'
number|'1'
op|']'
op|']'
newline|'\n'
name|'parts'
op|'.'
name|'append'
op|'('
op|'{'
name|'None'
op|':'
number|'0'
op|','
string|"'.1'"
op|':'
number|'1'
op|','
string|"'.2'"
op|':'
number|'2'
op|'}'
op|'['
name|'match'
op|'.'
name|'groups'
op|'('
op|')'
op|'['
op|'-'
number|'1'
op|']'
op|']'
op|')'
newline|'\n'
name|'return'
name|'Version'
op|'('
op|'*'
name|'parts'
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
DECL|member|on_or_after
dedent|''
name|'def'
name|'on_or_after'
op|'('
name|'self'
op|','
name|'other'
op|')'
op|':'
newline|'\n'
indent|'    '
name|'return'
op|'('
name|'self'
op|'.'
name|'major'
op|'>'
name|'other'
op|'.'
name|'major'
name|'or'
name|'self'
op|'.'
name|'major'
op|'=='
name|'other'
op|'.'
name|'major'
name|'and'
nl|'\n'
op|'('
name|'self'
op|'.'
name|'minor'
op|'>'
name|'other'
op|'.'
name|'minor'
name|'or'
name|'self'
op|'.'
name|'minor'
op|'=='
name|'other'
op|'.'
name|'minor'
name|'and'
nl|'\n'
op|'('
name|'self'
op|'.'
name|'bugfix'
op|'>'
name|'other'
op|'.'
name|'bugfix'
name|'or'
name|'self'
op|'.'
name|'bugfix'
op|'=='
name|'other'
op|'.'
name|'bugfix'
name|'and'
nl|'\n'
name|'self'
op|'.'
name|'prerelease'
op|'>='
name|'other'
op|'.'
name|'prerelease'
op|')'
op|')'
op|')'
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
name|'output'
op|'='
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
dedent|''
name|'return'
name|'output'
op|'.'
name|'decode'
op|'('
string|"'utf-8'"
op|')'
newline|'\n'
nl|'\n'
DECL|function|update_file
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
comment|'# branch types are "release", "stable" and "unstable"'
nl|'\n'
DECL|class|BranchType
dedent|''
name|'class'
name|'BranchType'
op|'('
name|'Enum'
op|')'
op|':'
newline|'\n'
DECL|variable|unstable
indent|'  '
name|'unstable'
op|'='
number|'1'
newline|'\n'
DECL|variable|stable
name|'stable'
op|'='
number|'2'
newline|'\n'
DECL|variable|release
name|'release'
op|'='
number|'3'
newline|'\n'
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
string|"'git status'"
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
string|"b'On branch '"
op|')'
op|':'
newline|'\n'
indent|'      '
name|'branchName'
op|'='
name|'line'
op|'.'
name|'split'
op|'('
string|"b' '"
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
string|"'git status missing branch name'"
op|')'
newline|'\n'
nl|'\n'
dedent|''
name|'if'
name|'branchName'
op|'=='
string|"b'master'"
op|':'
newline|'\n'
indent|'    '
name|'return'
name|'BranchType'
op|'.'
name|'unstable'
newline|'\n'
dedent|''
name|'if'
name|'re'
op|'.'
name|'match'
op|'('
string|"r'branch_(\\d+)x'"
op|','
name|'branchName'
op|'.'
name|'decode'
op|'('
string|"'UTF-8'"
op|')'
op|')'
op|':'
newline|'\n'
indent|'    '
name|'return'
name|'BranchType'
op|'.'
name|'stable'
newline|'\n'
dedent|''
name|'if'
name|'re'
op|'.'
name|'match'
op|'('
string|"r'branch_(\\d+)_(\\d+)'"
op|','
name|'branchName'
op|'.'
name|'decode'
op|'('
string|"'UTF-8'"
op|')'
op|')'
op|':'
newline|'\n'
indent|'    '
name|'return'
name|'BranchType'
op|'.'
name|'release'
newline|'\n'
dedent|''
name|'raise'
name|'Exception'
op|'('
string|"'Cannot run %s on feature branch'"
op|'%'
name|'sys'
op|'.'
name|'argv'
op|'['
number|'0'
op|']'
op|'.'
name|'rsplit'
op|'('
string|"'/'"
op|','
number|'1'
op|')'
op|'['
op|'-'
number|'1'
op|']'
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
DECL|function|find_current_version
name|'def'
name|'find_current_version'
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
dedent|''
name|'if'
name|'__name__'
op|'=='
string|"'__main__'"
op|':'
newline|'\n'
indent|'  '
name|'print'
op|'('
string|"'This is only a support module, it cannot be run'"
op|')'
newline|'\n'
name|'sys'
op|'.'
name|'exit'
op|'('
number|'1'
op|')'
newline|'\n'
dedent|''
endmarker|''
end_unit
