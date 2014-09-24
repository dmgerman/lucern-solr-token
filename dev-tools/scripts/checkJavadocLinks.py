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
name|'traceback'
newline|'\n'
name|'import'
name|'os'
newline|'\n'
name|'import'
name|'sys'
newline|'\n'
name|'import'
name|'re'
newline|'\n'
name|'from'
name|'html'
op|'.'
name|'parser'
name|'import'
name|'HTMLParser'
op|','
name|'HTMLParseError'
newline|'\n'
name|'import'
name|'urllib'
op|'.'
name|'parse'
name|'as'
name|'urlparse'
newline|'\n'
nl|'\n'
DECL|variable|reHyperlink
name|'reHyperlink'
op|'='
name|'re'
op|'.'
name|'compile'
op|'('
string|"r'<a(\\s+.*?)>'"
op|','
name|'re'
op|'.'
name|'I'
op|')'
newline|'\n'
name|'reAtt'
op|'='
name|'re'
op|'.'
name|'compile'
op|'('
string|'r"""(?:\\s+([a-z]+)\\s*=\\s*("[^"]*"|\'[^\']?\'|[^\'"\\s]+))+"""'
op|','
name|'re'
op|'.'
name|'I'
op|')'
newline|'\n'
nl|'\n'
comment|'# Char ::= #x9 | #xA | #xD | [#x20-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF] /* any Unicode character, excluding the surrogate blocks, FFFE, and FFFF. */'
nl|'\n'
DECL|variable|reValidChar
name|'reValidChar'
op|'='
name|'re'
op|'.'
name|'compile'
op|'('
string|'"^[^\\u0000-\\u0008\\u000B-\\u000C\\u000E-\\u001F\\uFFFE\\uFFFF]*$"'
op|')'
newline|'\n'
nl|'\n'
comment|"# silly emacs: '"
nl|'\n'
nl|'\n'
DECL|class|FindHyperlinks
name|'class'
name|'FindHyperlinks'
op|'('
name|'HTMLParser'
op|')'
op|':'
newline|'\n'
nl|'\n'
DECL|member|__init__
indent|'  '
name|'def'
name|'__init__'
op|'('
name|'self'
op|','
name|'baseURL'
op|')'
op|':'
newline|'\n'
indent|'    '
name|'HTMLParser'
op|'.'
name|'__init__'
op|'('
name|'self'
op|')'
newline|'\n'
name|'self'
op|'.'
name|'stack'
op|'='
op|'['
op|']'
newline|'\n'
name|'self'
op|'.'
name|'anchors'
op|'='
name|'set'
op|'('
op|')'
newline|'\n'
name|'self'
op|'.'
name|'links'
op|'='
op|'['
op|']'
newline|'\n'
name|'self'
op|'.'
name|'baseURL'
op|'='
name|'baseURL'
newline|'\n'
name|'self'
op|'.'
name|'printed'
op|'='
name|'False'
newline|'\n'
nl|'\n'
DECL|member|handle_starttag
dedent|''
name|'def'
name|'handle_starttag'
op|'('
name|'self'
op|','
name|'tag'
op|','
name|'attrs'
op|')'
op|':'
newline|'\n'
comment|"# NOTE: I don't think 'a' should be in here. But try debugging "
nl|'\n'
comment|'# NumericRangeQuery.html. (Could be javadocs bug, its a generic type...)'
nl|'\n'
indent|'    '
name|'if'
name|'tag'
name|'not'
name|'in'
op|'('
string|"'link'"
op|','
string|"'meta'"
op|','
string|"'frame'"
op|','
string|"'br'"
op|','
string|"'hr'"
op|','
string|"'p'"
op|','
string|"'li'"
op|','
string|"'img'"
op|','
string|"'col'"
op|','
string|"'a'"
op|')'
op|':'
newline|'\n'
indent|'      '
name|'self'
op|'.'
name|'stack'
op|'.'
name|'append'
op|'('
name|'tag'
op|')'
newline|'\n'
dedent|''
name|'if'
name|'tag'
op|'=='
string|"'a'"
op|':'
newline|'\n'
indent|'      '
name|'name'
op|'='
name|'None'
newline|'\n'
name|'href'
op|'='
name|'None'
newline|'\n'
name|'for'
name|'attName'
op|','
name|'attValue'
name|'in'
name|'attrs'
op|':'
newline|'\n'
indent|'        '
name|'if'
name|'attName'
op|'=='
string|"'name'"
op|':'
newline|'\n'
indent|'          '
name|'name'
op|'='
name|'attValue'
newline|'\n'
dedent|''
name|'elif'
name|'attName'
op|'=='
string|"'href'"
op|':'
newline|'\n'
indent|'          '
name|'href'
op|'='
name|'attValue'
newline|'\n'
nl|'\n'
dedent|''
dedent|''
name|'if'
name|'name'
name|'is'
name|'not'
name|'None'
op|':'
newline|'\n'
indent|'        '
name|'assert'
name|'href'
name|'is'
name|'None'
newline|'\n'
name|'if'
name|'name'
name|'in'
name|'self'
op|'.'
name|'anchors'
op|':'
newline|'\n'
indent|'          '
name|'if'
name|'name'
name|'in'
op|'('
string|"'serializedForm'"
op|','
nl|'\n'
string|"'serialized_methods'"
op|','
nl|'\n'
string|"'readObject(java.io.ObjectInputStream)'"
op|','
nl|'\n'
string|"'writeObject(java.io.ObjectOutputStream)'"
op|')'
name|'and'
name|'self'
op|'.'
name|'baseURL'
op|'.'
name|'endswith'
op|'('
string|"'/serialized-form.html'"
op|')'
op|':'
newline|'\n'
comment|"# Seems like a bug in Javadoc generation... you can't have"
nl|'\n'
comment|'# same anchor name more than once...'
nl|'\n'
indent|'            '
name|'pass'
newline|'\n'
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'            '
name|'self'
op|'.'
name|'printFile'
op|'('
op|')'
newline|'\n'
name|'raise'
name|'RuntimeError'
op|'('
string|'\'anchor "%s" appears more than once\''
op|'%'
name|'name'
op|')'
newline|'\n'
dedent|''
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'          '
name|'self'
op|'.'
name|'anchors'
op|'.'
name|'add'
op|'('
name|'name'
op|')'
newline|'\n'
dedent|''
dedent|''
name|'elif'
name|'href'
name|'is'
name|'not'
name|'None'
op|':'
newline|'\n'
indent|'        '
name|'assert'
name|'name'
name|'is'
name|'None'
newline|'\n'
name|'href'
op|'='
name|'href'
op|'.'
name|'strip'
op|'('
op|')'
newline|'\n'
name|'self'
op|'.'
name|'links'
op|'.'
name|'append'
op|'('
name|'urlparse'
op|'.'
name|'urljoin'
op|'('
name|'self'
op|'.'
name|'baseURL'
op|','
name|'href'
op|')'
op|')'
newline|'\n'
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'        '
name|'if'
name|'self'
op|'.'
name|'baseURL'
op|'.'
name|'endswith'
op|'('
string|"'/AttributeSource.html'"
op|')'
op|':'
newline|'\n'
comment|"# LUCENE-4010: AttributeSource's javadocs has an unescaped <A> generics!!  Seems to be a javadocs bug... (fixed in Java 7)"
nl|'\n'
indent|'          '
name|'pass'
newline|'\n'
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'          '
name|'raise'
name|'RuntimeError'
op|'('
string|"'couldn\\'t find an href nor name in link in %s: only got these attrs: %s'"
op|'%'
op|'('
name|'self'
op|'.'
name|'baseURL'
op|','
name|'attrs'
op|')'
op|')'
newline|'\n'
nl|'\n'
DECL|member|handle_endtag
dedent|''
dedent|''
dedent|''
dedent|''
name|'def'
name|'handle_endtag'
op|'('
name|'self'
op|','
name|'tag'
op|')'
op|':'
newline|'\n'
indent|'    '
name|'if'
name|'tag'
name|'in'
op|'('
string|"'link'"
op|','
string|"'meta'"
op|','
string|"'frame'"
op|','
string|"'br'"
op|','
string|"'hr'"
op|','
string|"'p'"
op|','
string|"'li'"
op|','
string|"'img'"
op|','
string|"'col'"
op|','
string|"'a'"
op|')'
op|':'
newline|'\n'
indent|'      '
name|'return'
newline|'\n'
nl|'\n'
dedent|''
name|'if'
name|'len'
op|'('
name|'self'
op|'.'
name|'stack'
op|')'
op|'=='
number|'0'
op|':'
newline|'\n'
indent|'      '
name|'raise'
name|'RuntimeError'
op|'('
string|"'%s %s:%s: saw </%s> no opening <%s>'"
op|'%'
op|'('
name|'self'
op|'.'
name|'baseURL'
op|','
name|'self'
op|'.'
name|'getpos'
op|'('
op|')'
op|'['
number|'0'
op|']'
op|','
name|'self'
op|'.'
name|'getpos'
op|'('
op|')'
op|'['
number|'1'
op|']'
op|','
name|'tag'
op|','
name|'self'
op|'.'
name|'stack'
op|'['
op|'-'
number|'1'
op|']'
op|')'
op|')'
newline|'\n'
nl|'\n'
dedent|''
name|'if'
name|'self'
op|'.'
name|'stack'
op|'['
op|'-'
number|'1'
op|']'
op|'=='
name|'tag'
op|':'
newline|'\n'
indent|'      '
name|'self'
op|'.'
name|'stack'
op|'.'
name|'pop'
op|'('
op|')'
newline|'\n'
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'      '
name|'raise'
name|'RuntimeError'
op|'('
string|"'%s %s:%s: saw </%s> but expected </%s>'"
op|'%'
op|'('
name|'self'
op|'.'
name|'baseURL'
op|','
name|'self'
op|'.'
name|'getpos'
op|'('
op|')'
op|'['
number|'0'
op|']'
op|','
name|'self'
op|'.'
name|'getpos'
op|'('
op|')'
op|'['
number|'1'
op|']'
op|','
name|'tag'
op|','
name|'self'
op|'.'
name|'stack'
op|'['
op|'-'
number|'1'
op|']'
op|')'
op|')'
newline|'\n'
nl|'\n'
DECL|member|printFile
dedent|''
dedent|''
name|'def'
name|'printFile'
op|'('
name|'self'
op|')'
op|':'
newline|'\n'
indent|'    '
name|'if'
name|'not'
name|'self'
op|'.'
name|'printed'
op|':'
newline|'\n'
indent|'      '
name|'print'
op|'('
op|')'
newline|'\n'
name|'print'
op|'('
string|"'  '"
op|'+'
name|'self'
op|'.'
name|'baseURL'
op|')'
newline|'\n'
name|'self'
op|'.'
name|'printed'
op|'='
name|'True'
newline|'\n'
nl|'\n'
DECL|function|parse
dedent|''
dedent|''
dedent|''
name|'def'
name|'parse'
op|'('
name|'baseURL'
op|','
name|'html'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'global'
name|'failures'
newline|'\n'
comment|'# look for broken unicode'
nl|'\n'
name|'if'
name|'not'
name|'reValidChar'
op|'.'
name|'match'
op|'('
name|'html'
op|')'
op|':'
newline|'\n'
indent|'    '
name|'print'
op|'('
string|"' WARNING: invalid characters detected in: %s'"
op|'%'
name|'baseURL'
op|')'
newline|'\n'
name|'failures'
op|'='
name|'True'
newline|'\n'
name|'return'
op|'['
op|']'
op|','
op|'['
op|']'
newline|'\n'
nl|'\n'
dedent|''
name|'parser'
op|'='
name|'FindHyperlinks'
op|'('
name|'baseURL'
op|')'
newline|'\n'
name|'try'
op|':'
newline|'\n'
indent|'    '
name|'parser'
op|'.'
name|'feed'
op|'('
name|'html'
op|')'
newline|'\n'
name|'parser'
op|'.'
name|'close'
op|'('
op|')'
newline|'\n'
dedent|''
name|'except'
name|'HTMLParseError'
op|':'
newline|'\n'
indent|'    '
name|'parser'
op|'.'
name|'printFile'
op|'('
op|')'
newline|'\n'
name|'print'
op|'('
string|"'  WARNING: failed to parse %s:'"
op|'%'
name|'baseURL'
op|')'
newline|'\n'
name|'traceback'
op|'.'
name|'print_exc'
op|'('
name|'file'
op|'='
name|'sys'
op|'.'
name|'stdout'
op|')'
newline|'\n'
name|'failures'
op|'='
name|'True'
newline|'\n'
name|'return'
op|'['
op|']'
op|','
op|'['
op|']'
newline|'\n'
nl|'\n'
comment|"#print '    %d links, %d anchors' % \\"
nl|'\n'
comment|'#      (len(parser.links), len(parser.anchors))'
nl|'\n'
dedent|''
name|'return'
name|'parser'
op|'.'
name|'links'
op|','
name|'parser'
op|'.'
name|'anchors'
newline|'\n'
nl|'\n'
DECL|variable|failures
dedent|''
name|'failures'
op|'='
name|'False'
newline|'\n'
nl|'\n'
DECL|function|checkAll
name|'def'
name|'checkAll'
op|'('
name|'dirName'
op|')'
op|':'
newline|'\n'
indent|'  '
string|'"""\n  Checks *.html (recursively) under this directory.\n  """'
newline|'\n'
nl|'\n'
name|'global'
name|'failures'
newline|'\n'
nl|'\n'
comment|'# Find/parse all HTML files first'
nl|'\n'
name|'print'
op|'('
op|')'
newline|'\n'
name|'print'
op|'('
string|"'Crawl/parse...'"
op|')'
newline|'\n'
name|'allFiles'
op|'='
op|'{'
op|'}'
newline|'\n'
nl|'\n'
name|'if'
name|'os'
op|'.'
name|'path'
op|'.'
name|'isfile'
op|'('
name|'dirName'
op|')'
op|':'
newline|'\n'
indent|'    '
name|'root'
op|','
name|'fileName'
op|'='
name|'os'
op|'.'
name|'path'
op|'.'
name|'split'
op|'('
name|'dirName'
op|')'
newline|'\n'
name|'iter'
op|'='
op|'('
op|'('
name|'root'
op|','
op|'['
op|']'
op|','
op|'['
name|'fileName'
op|']'
op|')'
op|','
op|')'
newline|'\n'
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'    '
name|'iter'
op|'='
name|'os'
op|'.'
name|'walk'
op|'('
name|'dirName'
op|')'
newline|'\n'
nl|'\n'
dedent|''
name|'for'
name|'root'
op|','
name|'dirs'
op|','
name|'files'
name|'in'
name|'iter'
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
name|'main'
op|','
name|'ext'
op|'='
name|'os'
op|'.'
name|'path'
op|'.'
name|'splitext'
op|'('
name|'f'
op|')'
newline|'\n'
name|'ext'
op|'='
name|'ext'
op|'.'
name|'lower'
op|'('
op|')'
newline|'\n'
nl|'\n'
comment|'# maybe?:'
nl|'\n'
comment|"# and main not in ('serialized-form'):"
nl|'\n'
name|'if'
name|'ext'
name|'in'
op|'('
string|"'.htm'"
op|','
string|"'.html'"
op|')'
name|'and'
name|'not'
name|'f'
op|'.'
name|'startswith'
op|'('
string|"'.#'"
op|')'
name|'and'
name|'main'
name|'not'
name|'in'
op|'('
string|"'deprecated-list'"
op|','
op|')'
op|':'
newline|'\n'
comment|'# Somehow even w/ java 7 generaged javadocs,'
nl|'\n'
comment|'# deprecated-list.html can fail to escape generics types'
nl|'\n'
indent|'        '
name|'fullPath'
op|'='
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
op|'.'
name|'replace'
op|'('
name|'os'
op|'.'
name|'path'
op|'.'
name|'sep'
op|','
string|"'/'"
op|')'
newline|'\n'
name|'fullPath'
op|'='
string|"'file:%s'"
op|'%'
name|'urlparse'
op|'.'
name|'quote'
op|'('
name|'fullPath'
op|')'
newline|'\n'
comment|'# parse and unparse the URL to "normalize" it'
nl|'\n'
name|'fullPath'
op|'='
name|'urlparse'
op|'.'
name|'urlunparse'
op|'('
name|'urlparse'
op|'.'
name|'urlparse'
op|'('
name|'fullPath'
op|')'
op|')'
newline|'\n'
comment|"#print '  %s' % fullPath"
nl|'\n'
name|'allFiles'
op|'['
name|'fullPath'
op|']'
op|'='
name|'parse'
op|'('
name|'fullPath'
op|','
name|'open'
op|'('
string|"'%s/%s'"
op|'%'
op|'('
name|'root'
op|','
name|'f'
op|')'
op|','
name|'encoding'
op|'='
string|"'UTF-8'"
op|')'
op|'.'
name|'read'
op|'('
op|')'
op|')'
newline|'\n'
nl|'\n'
comment|'# ... then verify:'
nl|'\n'
dedent|''
dedent|''
dedent|''
name|'print'
op|'('
op|')'
newline|'\n'
name|'print'
op|'('
string|"'Verify...'"
op|')'
newline|'\n'
name|'for'
name|'fullPath'
op|','
op|'('
name|'links'
op|','
name|'anchors'
op|')'
name|'in'
name|'allFiles'
op|'.'
name|'items'
op|'('
op|')'
op|':'
newline|'\n'
comment|'#print fullPath'
nl|'\n'
indent|'    '
name|'printed'
op|'='
name|'False'
newline|'\n'
name|'for'
name|'link'
name|'in'
name|'links'
op|':'
newline|'\n'
nl|'\n'
indent|'      '
name|'origLink'
op|'='
name|'link'
newline|'\n'
nl|'\n'
comment|'# TODO: use urlparse?'
nl|'\n'
name|'idx'
op|'='
name|'link'
op|'.'
name|'find'
op|'('
string|"'#'"
op|')'
newline|'\n'
name|'if'
name|'idx'
op|'!='
op|'-'
number|'1'
op|':'
newline|'\n'
indent|'        '
name|'anchor'
op|'='
name|'link'
op|'['
name|'idx'
op|'+'
number|'1'
op|':'
op|']'
newline|'\n'
name|'link'
op|'='
name|'link'
op|'['
op|':'
name|'idx'
op|']'
newline|'\n'
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'        '
name|'anchor'
op|'='
name|'None'
newline|'\n'
nl|'\n'
comment|'# remove any whitespace from the middle of the link'
nl|'\n'
dedent|''
name|'link'
op|'='
string|"''"
op|'.'
name|'join'
op|'('
name|'link'
op|'.'
name|'split'
op|'('
op|')'
op|')'
newline|'\n'
nl|'\n'
name|'idx'
op|'='
name|'link'
op|'.'
name|'find'
op|'('
string|"'?'"
op|')'
newline|'\n'
name|'if'
name|'idx'
op|'!='
op|'-'
number|'1'
op|':'
newline|'\n'
indent|'        '
name|'link'
op|'='
name|'link'
op|'['
op|':'
name|'idx'
op|']'
newline|'\n'
nl|'\n'
comment|'# TODO: normalize path sep for windows...'
nl|'\n'
dedent|''
name|'if'
name|'link'
op|'.'
name|'startswith'
op|'('
string|"'http://'"
op|')'
name|'or'
name|'link'
op|'.'
name|'startswith'
op|'('
string|"'https://'"
op|')'
op|':'
newline|'\n'
comment|"# don't check external links"
nl|'\n'
nl|'\n'
indent|'        '
name|'if'
name|'link'
op|'.'
name|'find'
op|'('
string|"'lucene.apache.org/java/docs/mailinglists.html'"
op|')'
op|'!='
op|'-'
number|'1'
op|':'
newline|'\n'
comment|'# OK'
nl|'\n'
indent|'          '
name|'pass'
newline|'\n'
dedent|''
name|'elif'
name|'link'
op|'=='
string|"'http://lucene.apache.org/core/'"
op|':'
newline|'\n'
comment|'# OK'
nl|'\n'
indent|'          '
name|'pass'
newline|'\n'
dedent|''
name|'elif'
name|'link'
op|'=='
string|"'http://lucene.apache.org/solr/'"
op|':'
newline|'\n'
comment|'# OK'
nl|'\n'
indent|'          '
name|'pass'
newline|'\n'
dedent|''
name|'elif'
name|'link'
op|'.'
name|'find'
op|'('
string|"'lucene.apache.org/java/docs/discussion.html'"
op|')'
op|'!='
op|'-'
number|'1'
op|':'
newline|'\n'
comment|'# OK'
nl|'\n'
indent|'          '
name|'pass'
newline|'\n'
dedent|''
name|'elif'
name|'link'
op|'.'
name|'find'
op|'('
string|"'lucene.apache.org/core/discussion.html'"
op|')'
op|'!='
op|'-'
number|'1'
op|':'
newline|'\n'
comment|'# OK'
nl|'\n'
indent|'          '
name|'pass'
newline|'\n'
dedent|''
name|'elif'
name|'link'
op|'.'
name|'find'
op|'('
string|"'lucene.apache.org/solr/mirrors-solr-latest-redir.html'"
op|')'
op|'!='
op|'-'
number|'1'
op|':'
newline|'\n'
comment|'# OK'
nl|'\n'
indent|'          '
name|'pass'
newline|'\n'
dedent|''
name|'elif'
name|'link'
op|'.'
name|'find'
op|'('
string|"'lucene.apache.org/solr/discussion.html'"
op|')'
op|'!='
op|'-'
number|'1'
op|':'
newline|'\n'
comment|'# OK'
nl|'\n'
indent|'          '
name|'pass'
newline|'\n'
dedent|''
name|'elif'
name|'link'
op|'.'
name|'find'
op|'('
string|"'lucene.apache.org/solr/features.html'"
op|')'
op|'!='
op|'-'
number|'1'
op|':'
newline|'\n'
comment|'# OK'
nl|'\n'
indent|'          '
name|'pass'
newline|'\n'
dedent|''
name|'elif'
op|'('
name|'link'
op|'.'
name|'find'
op|'('
string|"'svn.apache.org'"
op|')'
op|'!='
op|'-'
number|'1'
nl|'\n'
name|'or'
name|'link'
op|'.'
name|'find'
op|'('
string|"'lucene.apache.org'"
op|')'
op|'!='
op|'-'
number|'1'
op|')'
name|'and'
name|'os'
op|'.'
name|'path'
op|'.'
name|'basename'
op|'('
name|'fullPath'
op|')'
op|'!='
string|"'Changes.html'"
op|':'
newline|'\n'
indent|'          '
name|'if'
name|'not'
name|'printed'
op|':'
newline|'\n'
indent|'            '
name|'printed'
op|'='
name|'True'
newline|'\n'
name|'print'
op|'('
op|')'
newline|'\n'
name|'print'
op|'('
name|'fullPath'
op|')'
newline|'\n'
dedent|''
name|'print'
op|'('
string|"'  BAD EXTERNAL LINK: %s'"
op|'%'
name|'link'
op|')'
newline|'\n'
dedent|''
dedent|''
name|'elif'
name|'link'
op|'.'
name|'startswith'
op|'('
string|"'mailto:'"
op|')'
op|':'
newline|'\n'
indent|'        '
name|'if'
name|'link'
op|'.'
name|'find'
op|'('
string|"'@lucene.apache.org'"
op|')'
op|'=='
op|'-'
number|'1'
name|'and'
name|'link'
op|'.'
name|'find'
op|'('
string|"'@apache.org'"
op|')'
op|'!='
op|'-'
number|'1'
op|':'
newline|'\n'
indent|'          '
name|'if'
name|'not'
name|'printed'
op|':'
newline|'\n'
indent|'            '
name|'printed'
op|'='
name|'True'
newline|'\n'
name|'print'
op|'('
op|')'
newline|'\n'
name|'print'
op|'('
name|'fullPath'
op|')'
newline|'\n'
dedent|''
name|'print'
op|'('
string|"'  BROKEN MAILTO (?): %s'"
op|'%'
name|'link'
op|')'
newline|'\n'
dedent|''
dedent|''
name|'elif'
name|'link'
op|'.'
name|'startswith'
op|'('
string|"'javascript:'"
op|')'
op|':'
newline|'\n'
comment|'# ok...?'
nl|'\n'
indent|'        '
name|'pass'
newline|'\n'
dedent|''
name|'elif'
string|"'org/apache/solr/client/solrj/beans/Field.html'"
name|'in'
name|'link'
op|':'
newline|'\n'
comment|'# see LUCENE-4011: this is a javadocs bug for constants '
nl|'\n'
comment|'# on annotations it seems?'
nl|'\n'
indent|'        '
name|'pass'
newline|'\n'
dedent|''
name|'elif'
name|'link'
op|'.'
name|'startswith'
op|'('
string|"'file:'"
op|')'
op|':'
newline|'\n'
indent|'        '
name|'if'
name|'link'
name|'not'
name|'in'
name|'allFiles'
op|':'
newline|'\n'
indent|'          '
name|'filepath'
op|'='
name|'urlparse'
op|'.'
name|'unquote'
op|'('
name|'urlparse'
op|'.'
name|'urlparse'
op|'('
name|'link'
op|')'
op|'.'
name|'path'
op|')'
newline|'\n'
name|'if'
name|'not'
op|'('
name|'os'
op|'.'
name|'path'
op|'.'
name|'exists'
op|'('
name|'filepath'
op|')'
name|'or'
name|'os'
op|'.'
name|'path'
op|'.'
name|'exists'
op|'('
name|'filepath'
op|'['
number|'1'
op|':'
op|']'
op|')'
op|')'
op|':'
newline|'\n'
indent|'            '
name|'if'
name|'not'
name|'printed'
op|':'
newline|'\n'
indent|'              '
name|'printed'
op|'='
name|'True'
newline|'\n'
name|'print'
op|'('
op|')'
newline|'\n'
name|'print'
op|'('
name|'fullPath'
op|')'
newline|'\n'
dedent|''
name|'print'
op|'('
string|"'  BROKEN LINK: %s'"
op|'%'
name|'link'
op|')'
newline|'\n'
dedent|''
dedent|''
dedent|''
name|'elif'
name|'anchor'
name|'is'
name|'not'
name|'None'
name|'and'
name|'anchor'
name|'not'
name|'in'
name|'allFiles'
op|'['
name|'link'
op|']'
op|'['
number|'1'
op|']'
op|':'
newline|'\n'
indent|'        '
name|'if'
name|'not'
name|'printed'
op|':'
newline|'\n'
indent|'          '
name|'printed'
op|'='
name|'True'
newline|'\n'
name|'print'
op|'('
op|')'
newline|'\n'
name|'print'
op|'('
name|'fullPath'
op|')'
newline|'\n'
dedent|''
name|'print'
op|'('
string|"'  BROKEN ANCHOR: %s'"
op|'%'
name|'origLink'
op|')'
newline|'\n'
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'        '
name|'if'
name|'not'
name|'printed'
op|':'
newline|'\n'
indent|'          '
name|'printed'
op|'='
name|'True'
newline|'\n'
name|'print'
op|'('
op|')'
newline|'\n'
name|'print'
op|'('
name|'fullPath'
op|')'
newline|'\n'
dedent|''
name|'print'
op|'('
string|"'  BROKEN URL SCHEME: %s'"
op|'%'
name|'origLink'
op|')'
newline|'\n'
dedent|''
dedent|''
name|'failures'
op|'='
name|'failures'
name|'or'
name|'printed'
newline|'\n'
nl|'\n'
dedent|''
name|'return'
name|'failures'
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
name|'if'
name|'checkAll'
op|'('
name|'sys'
op|'.'
name|'argv'
op|'['
number|'1'
op|']'
op|')'
op|':'
newline|'\n'
indent|'    '
name|'print'
op|'('
op|')'
newline|'\n'
name|'print'
op|'('
string|"'Broken javadocs links were found!'"
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
name|'sys'
op|'.'
name|'exit'
op|'('
number|'0'
op|')'
newline|'\n'
nl|'\n'
dedent|''
endmarker|''
end_unit
