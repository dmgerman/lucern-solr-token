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
string|'"""\nThis will generate a movie data set of 1100 records.\nThese are the first 1100 movies which appear when querying the Freebase of type \'/film/film\'.\nHere is the link to the freebase page - https://www.freebase.com/film/film?schema=\n\nUsage - python3 film_data_generator.py\n"""'
newline|'\n'
nl|'\n'
name|'import'
name|'csv'
newline|'\n'
name|'import'
name|'copy'
newline|'\n'
name|'import'
name|'json'
newline|'\n'
name|'import'
name|'codecs'
newline|'\n'
name|'import'
name|'datetime'
newline|'\n'
name|'import'
name|'urllib'
op|'.'
name|'parse'
newline|'\n'
name|'import'
name|'urllib'
op|'.'
name|'request'
newline|'\n'
name|'import'
name|'xml'
op|'.'
name|'etree'
op|'.'
name|'cElementTree'
name|'as'
name|'ET'
newline|'\n'
name|'from'
name|'xml'
op|'.'
name|'dom'
name|'import'
name|'minidom'
newline|'\n'
nl|'\n'
DECL|variable|MAX_ITERATIONS
name|'MAX_ITERATIONS'
op|'='
number|'10'
comment|'#10 limits it to 1100 docs'
newline|'\n'
nl|'\n'
comment|'# You need an API Key by Google to run this'
nl|'\n'
DECL|variable|API_KEY
name|'API_KEY'
op|'='
string|"'<insert your Google developer API key>'"
newline|'\n'
DECL|variable|service_url
name|'service_url'
op|'='
string|"'https://www.googleapis.com/freebase/v1/mqlread'"
newline|'\n'
DECL|variable|query
name|'query'
op|'='
op|'['
op|'{'
nl|'\n'
string|'"id"'
op|':'
name|'None'
op|','
nl|'\n'
string|'"name"'
op|':'
name|'None'
op|','
nl|'\n'
string|'"initial_release_date"'
op|':'
name|'None'
op|','
nl|'\n'
string|'"directed_by"'
op|':'
op|'['
op|']'
op|','
nl|'\n'
string|'"genre"'
op|':'
op|'['
op|']'
op|','
nl|'\n'
string|'"type"'
op|':'
string|'"/film/film"'
op|','
nl|'\n'
string|'"initial_release_date>"'
op|':'
string|'"2000"'
nl|'\n'
op|'}'
op|']'
newline|'\n'
nl|'\n'
DECL|function|gen_csv
name|'def'
name|'gen_csv'
op|'('
name|'filmlist'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'filmlistDup'
op|'='
name|'copy'
op|'.'
name|'deepcopy'
op|'('
name|'filmlist'
op|')'
newline|'\n'
comment|'#Convert multi-valued to % delimited string'
nl|'\n'
name|'for'
name|'film'
name|'in'
name|'filmlistDup'
op|':'
newline|'\n'
indent|'      '
name|'for'
name|'key'
name|'in'
name|'film'
op|':'
newline|'\n'
indent|'        '
name|'if'
name|'isinstance'
op|'('
name|'film'
op|'['
name|'key'
op|']'
op|','
name|'list'
op|')'
op|':'
newline|'\n'
indent|'          '
name|'film'
op|'['
name|'key'
op|']'
op|'='
string|"'|'"
op|'.'
name|'join'
op|'('
name|'film'
op|'['
name|'key'
op|']'
op|')'
newline|'\n'
dedent|''
dedent|''
dedent|''
name|'keys'
op|'='
op|'['
string|"'name'"
op|','
string|"'directed_by'"
op|','
string|"'genre'"
op|','
string|"'type'"
op|','
string|"'id'"
op|','
string|"'initial_release_date'"
op|']'
newline|'\n'
name|'with'
name|'open'
op|'('
string|"'films.csv'"
op|','
string|"'w'"
op|','
name|'newline'
op|'='
string|"''"
op|','
name|'encoding'
op|'='
string|"'utf8'"
op|')'
name|'as'
name|'csvfile'
op|':'
newline|'\n'
indent|'    '
name|'dict_writer'
op|'='
name|'csv'
op|'.'
name|'DictWriter'
op|'('
name|'csvfile'
op|','
name|'keys'
op|')'
newline|'\n'
name|'dict_writer'
op|'.'
name|'writeheader'
op|'('
op|')'
newline|'\n'
name|'dict_writer'
op|'.'
name|'writerows'
op|'('
name|'filmlistDup'
op|')'
newline|'\n'
nl|'\n'
DECL|function|gen_json
dedent|''
dedent|''
name|'def'
name|'gen_json'
op|'('
name|'filmlist'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'filmlistDup'
op|'='
name|'copy'
op|'.'
name|'deepcopy'
op|'('
name|'filmlist'
op|')'
newline|'\n'
name|'with'
name|'open'
op|'('
string|"'films.json'"
op|','
string|"'w'"
op|')'
name|'as'
name|'jsonfile'
op|':'
newline|'\n'
indent|'    '
name|'jsonfile'
op|'.'
name|'write'
op|'('
name|'json'
op|'.'
name|'dumps'
op|'('
name|'filmlist'
op|','
name|'indent'
op|'='
number|'2'
op|')'
op|')'
newline|'\n'
nl|'\n'
DECL|function|gen_xml
dedent|''
dedent|''
name|'def'
name|'gen_xml'
op|'('
name|'filmlist'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'root'
op|'='
name|'ET'
op|'.'
name|'Element'
op|'('
string|'"add"'
op|')'
newline|'\n'
name|'for'
name|'film'
name|'in'
name|'filmlist'
op|':'
newline|'\n'
indent|'    '
name|'doc'
op|'='
name|'ET'
op|'.'
name|'SubElement'
op|'('
name|'root'
op|','
string|'"doc"'
op|')'
newline|'\n'
name|'for'
name|'key'
name|'in'
name|'film'
op|':'
newline|'\n'
indent|'      '
name|'if'
name|'isinstance'
op|'('
name|'film'
op|'['
name|'key'
op|']'
op|','
name|'list'
op|')'
op|':'
newline|'\n'
indent|'        '
name|'for'
name|'value'
name|'in'
name|'film'
op|'['
name|'key'
op|']'
op|':'
newline|'\n'
indent|'          '
name|'field'
op|'='
name|'ET'
op|'.'
name|'SubElement'
op|'('
name|'doc'
op|','
string|'"field"'
op|')'
newline|'\n'
name|'field'
op|'.'
name|'set'
op|'('
string|'"name"'
op|','
name|'key'
op|')'
newline|'\n'
name|'field'
op|'.'
name|'text'
op|'='
name|'value'
newline|'\n'
dedent|''
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'        '
name|'field'
op|'='
name|'ET'
op|'.'
name|'SubElement'
op|'('
name|'doc'
op|','
string|'"field"'
op|')'
newline|'\n'
name|'field'
op|'.'
name|'set'
op|'('
string|'"name"'
op|','
name|'key'
op|')'
newline|'\n'
name|'field'
op|'.'
name|'text'
op|'='
name|'film'
op|'['
name|'key'
op|']'
newline|'\n'
dedent|''
dedent|''
dedent|''
name|'tree'
op|'='
name|'ET'
op|'.'
name|'ElementTree'
op|'('
name|'root'
op|')'
newline|'\n'
name|'with'
name|'open'
op|'('
string|"'films.xml'"
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
name|'minidom'
op|'.'
name|'parseString'
op|'('
name|'ET'
op|'.'
name|'tostring'
op|'('
name|'tree'
op|'.'
name|'getroot'
op|'('
op|')'
op|','
string|"'utf-8'"
op|')'
op|')'
op|'.'
name|'toprettyxml'
op|'('
name|'indent'
op|'='
string|'"  "'
op|')'
op|')'
newline|'\n'
nl|'\n'
DECL|function|do_query
dedent|''
dedent|''
name|'def'
name|'do_query'
op|'('
name|'filmlist'
op|','
name|'cursor'
op|'='
string|'""'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'params'
op|'='
op|'{'
nl|'\n'
string|"'query'"
op|':'
name|'json'
op|'.'
name|'dumps'
op|'('
name|'query'
op|')'
op|','
nl|'\n'
string|"'key'"
op|':'
name|'API_KEY'
op|','
nl|'\n'
string|"'cursor'"
op|':'
name|'cursor'
nl|'\n'
op|'}'
newline|'\n'
name|'url'
op|'='
name|'service_url'
op|'+'
string|"'?'"
op|'+'
name|'urllib'
op|'.'
name|'parse'
op|'.'
name|'urlencode'
op|'('
name|'params'
op|')'
newline|'\n'
name|'data'
op|'='
name|'urllib'
op|'.'
name|'request'
op|'.'
name|'urlopen'
op|'('
name|'url'
op|')'
op|'.'
name|'read'
op|'('
op|')'
op|'.'
name|'decode'
op|'('
string|"'utf-8'"
op|')'
newline|'\n'
name|'response'
op|'='
name|'json'
op|'.'
name|'loads'
op|'('
name|'data'
op|')'
newline|'\n'
name|'for'
name|'item'
name|'in'
name|'response'
op|'['
string|"'result'"
op|']'
op|':'
newline|'\n'
indent|'    '
name|'del'
name|'item'
op|'['
string|"'type'"
op|']'
comment|"# It's always /film/film. No point of adding this."
newline|'\n'
name|'try'
op|':'
newline|'\n'
indent|'      '
name|'datetime'
op|'.'
name|'datetime'
op|'.'
name|'strptime'
op|'('
name|'item'
op|'['
string|"'initial_release_date'"
op|']'
op|','
string|'"%Y-%m-%d"'
op|')'
newline|'\n'
dedent|''
name|'except'
name|'ValueError'
op|':'
newline|'\n'
comment|'#Date time not formatted properly. Keeping it simple by removing the date field from that doc'
nl|'\n'
indent|'      '
name|'del'
name|'item'
op|'['
string|"'initial_release_date'"
op|']'
newline|'\n'
dedent|''
name|'filmlist'
op|'.'
name|'append'
op|'('
name|'item'
op|')'
newline|'\n'
dedent|''
name|'return'
name|'response'
op|'.'
name|'get'
op|'('
string|'"cursor"'
op|')'
newline|'\n'
nl|'\n'
nl|'\n'
dedent|''
name|'if'
name|'__name__'
op|'=='
string|'"__main__"'
op|':'
newline|'\n'
DECL|variable|filmlist
indent|'  '
name|'filmlist'
op|'='
op|'['
op|']'
newline|'\n'
DECL|variable|cursor
name|'cursor'
op|'='
name|'do_query'
op|'('
name|'filmlist'
op|')'
newline|'\n'
DECL|variable|i
name|'i'
op|'='
number|'0'
newline|'\n'
name|'while'
op|'('
name|'cursor'
op|')'
op|':'
newline|'\n'
DECL|variable|cursor
indent|'      '
name|'cursor'
op|'='
name|'do_query'
op|'('
name|'filmlist'
op|','
name|'cursor'
op|')'
newline|'\n'
DECL|variable|i
name|'i'
op|'='
name|'i'
op|'+'
number|'1'
newline|'\n'
name|'if'
name|'i'
op|'=='
name|'MAX_ITERATIONS'
op|':'
newline|'\n'
indent|'          '
name|'break'
newline|'\n'
nl|'\n'
dedent|''
dedent|''
name|'gen_json'
op|'('
name|'filmlist'
op|')'
newline|'\n'
name|'gen_csv'
op|'('
name|'filmlist'
op|')'
newline|'\n'
name|'gen_xml'
op|'('
name|'filmlist'
op|')'
dedent|''
endmarker|''
end_unit
