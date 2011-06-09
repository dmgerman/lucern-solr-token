begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.common.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import
begin_comment
comment|/**  *  */
end_comment
begin_class
DECL|class|XML
specifier|public
class|class
name|XML
block|{
comment|//
comment|// copied from some of my personal code...  -YCS
comment|// table created from python script.
comment|// only have to escape quotes in attribute values, and don't really have to escape '>'
comment|// many chars less than 0x20 are *not* valid XML, even when escaped!
comment|// for example,<foo>&#0;<foo> is invalid XML.
DECL|field|chardata_escapes
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|chardata_escapes
init|=
block|{
literal|"#0;"
block|,
literal|"#1;"
block|,
literal|"#2;"
block|,
literal|"#3;"
block|,
literal|"#4;"
block|,
literal|"#5;"
block|,
literal|"#6;"
block|,
literal|"#7;"
block|,
literal|"#8;"
block|,
literal|null
block|,
literal|null
block|,
literal|"#11;"
block|,
literal|"#12;"
block|,
literal|null
block|,
literal|"#14;"
block|,
literal|"#15;"
block|,
literal|"#16;"
block|,
literal|"#17;"
block|,
literal|"#18;"
block|,
literal|"#19;"
block|,
literal|"#20;"
block|,
literal|"#21;"
block|,
literal|"#22;"
block|,
literal|"#23;"
block|,
literal|"#24;"
block|,
literal|"#25;"
block|,
literal|"#26;"
block|,
literal|"#27;"
block|,
literal|"#28;"
block|,
literal|"#29;"
block|,
literal|"#30;"
block|,
literal|"#31;"
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|"&amp;"
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|"&lt;"
block|,
literal|null
block|,
literal|"&gt;"
block|}
decl_stmt|;
DECL|field|attribute_escapes
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|attribute_escapes
init|=
block|{
literal|"#0;"
block|,
literal|"#1;"
block|,
literal|"#2;"
block|,
literal|"#3;"
block|,
literal|"#4;"
block|,
literal|"#5;"
block|,
literal|"#6;"
block|,
literal|"#7;"
block|,
literal|"#8;"
block|,
literal|null
block|,
literal|null
block|,
literal|"#11;"
block|,
literal|"#12;"
block|,
literal|null
block|,
literal|"#14;"
block|,
literal|"#15;"
block|,
literal|"#16;"
block|,
literal|"#17;"
block|,
literal|"#18;"
block|,
literal|"#19;"
block|,
literal|"#20;"
block|,
literal|"#21;"
block|,
literal|"#22;"
block|,
literal|"#23;"
block|,
literal|"#24;"
block|,
literal|"#25;"
block|,
literal|"#26;"
block|,
literal|"#27;"
block|,
literal|"#28;"
block|,
literal|"#29;"
block|,
literal|"#30;"
block|,
literal|"#31;"
block|,
literal|null
block|,
literal|null
block|,
literal|"&quot;"
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|"&amp;"
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|"&lt;"
block|}
decl_stmt|;
comment|/*****************************************    #Simple python script used to generate the escape table above.  -YCS    #    #use individual char arrays or one big char array for better efficiency    # or byte array?    #other={'&':'amp', '<':'lt', '>':'gt', "'":'apos', '"':'quot'}    #    other={'&':'amp', '<':'lt'}     maxi=ord(max(other.keys()))+1    table=[None] * maxi    #NOTE: invalid XML chars are "escaped" as #nn; *not*&#nn; because    #a real XML escape would cause many strict XML parsers to choke.    for i in range(0x20): table[i]='#%d;' % i    for i in '\n\r\t ': table[ord(i)]=None    for k,v in other.items():     table[ord(k)]='&%s;' % v     result=""    for i in range(maxi):      val=table[i]      if not val: val='null'      else: val='"%s"' % val      result += val + ','     print result    ****************************************/
comment|/*********  *  * @param str  * @param out  * @throws IOException  */
DECL|method|escapeCharData
specifier|public
specifier|static
name|void
name|escapeCharData
parameter_list|(
name|String
name|str
parameter_list|,
name|Writer
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|escape
argument_list|(
name|str
argument_list|,
name|out
argument_list|,
name|chardata_escapes
argument_list|)
expr_stmt|;
block|}
DECL|method|escapeAttributeValue
specifier|public
specifier|static
name|void
name|escapeAttributeValue
parameter_list|(
name|String
name|str
parameter_list|,
name|Writer
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|escape
argument_list|(
name|str
argument_list|,
name|out
argument_list|,
name|attribute_escapes
argument_list|)
expr_stmt|;
block|}
DECL|method|escapeAttributeValue
specifier|public
specifier|static
name|void
name|escapeAttributeValue
parameter_list|(
name|char
index|[]
name|chars
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|,
name|Writer
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|escape
argument_list|(
name|chars
argument_list|,
name|start
argument_list|,
name|length
argument_list|,
name|out
argument_list|,
name|attribute_escapes
argument_list|)
expr_stmt|;
block|}
DECL|method|writeXML
specifier|public
specifier|final
specifier|static
name|void
name|writeXML
parameter_list|(
name|Writer
name|out
parameter_list|,
name|String
name|tag
parameter_list|,
name|String
name|val
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|write
argument_list|(
literal|'<'
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|tag
argument_list|)
expr_stmt|;
if|if
condition|(
name|val
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
literal|'/'
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|'>'
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|write
argument_list|(
literal|'>'
argument_list|)
expr_stmt|;
name|escapeCharData
argument_list|(
name|val
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|'<'
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|'/'
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|tag
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|'>'
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** does NOT escape character data in val, must already be valid XML */
DECL|method|writeUnescapedXML
specifier|public
specifier|final
specifier|static
name|void
name|writeUnescapedXML
parameter_list|(
name|Writer
name|out
parameter_list|,
name|String
name|tag
parameter_list|,
name|String
name|val
parameter_list|,
name|Object
modifier|...
name|attrs
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|write
argument_list|(
literal|'<'
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|tag
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|attrs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|out
operator|.
name|write
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|attrs
index|[
name|i
operator|++
index|]
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|'='
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|attrs
index|[
name|i
index|]
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|val
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
literal|'/'
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|'>'
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|write
argument_list|(
literal|'>'
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|val
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|'<'
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|'/'
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|tag
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|'>'
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** escapes character data in val */
DECL|method|writeXML
specifier|public
specifier|final
specifier|static
name|void
name|writeXML
parameter_list|(
name|Writer
name|out
parameter_list|,
name|String
name|tag
parameter_list|,
name|String
name|val
parameter_list|,
name|Object
modifier|...
name|attrs
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|write
argument_list|(
literal|'<'
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|tag
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|attrs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|out
operator|.
name|write
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|attrs
index|[
name|i
operator|++
index|]
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|'='
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
name|escapeAttributeValue
argument_list|(
name|attrs
index|[
name|i
index|]
operator|.
name|toString
argument_list|()
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|val
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
literal|'/'
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|'>'
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|write
argument_list|(
literal|'>'
argument_list|)
expr_stmt|;
name|escapeCharData
argument_list|(
name|val
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|'<'
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|'/'
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|tag
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|'>'
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** escapes character data in val */
DECL|method|writeXML
specifier|public
specifier|static
name|void
name|writeXML
parameter_list|(
name|Writer
name|out
parameter_list|,
name|String
name|tag
parameter_list|,
name|String
name|val
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attrs
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|write
argument_list|(
literal|'<'
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|tag
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|attrs
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|out
operator|.
name|write
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|'='
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
name|escapeAttributeValue
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|val
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
literal|'/'
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|'>'
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|write
argument_list|(
literal|'>'
argument_list|)
expr_stmt|;
name|escapeCharData
argument_list|(
name|val
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|'<'
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|'/'
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|tag
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|'>'
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|escape
specifier|private
specifier|static
name|void
name|escape
parameter_list|(
name|char
index|[]
name|chars
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|,
name|Writer
name|out
parameter_list|,
name|String
index|[]
name|escapes
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|i
init|=
name|offset
init|;
name|i
operator|<
name|length
condition|;
name|i
operator|++
control|)
block|{
name|char
name|ch
init|=
name|chars
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|ch
operator|<
name|escapes
operator|.
name|length
condition|)
block|{
name|String
name|replacement
init|=
name|escapes
index|[
name|ch
index|]
decl_stmt|;
if|if
condition|(
name|replacement
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
name|replacement
argument_list|)
expr_stmt|;
continue|continue;
block|}
block|}
name|out
operator|.
name|write
argument_list|(
name|ch
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|escape
specifier|private
specifier|static
name|void
name|escape
parameter_list|(
name|String
name|str
parameter_list|,
name|Writer
name|out
parameter_list|,
name|String
index|[]
name|escapes
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|str
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|char
name|ch
init|=
name|str
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|ch
operator|<
name|escapes
operator|.
name|length
condition|)
block|{
name|String
name|replacement
init|=
name|escapes
index|[
name|ch
index|]
decl_stmt|;
if|if
condition|(
name|replacement
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
name|replacement
argument_list|)
expr_stmt|;
continue|continue;
block|}
block|}
name|out
operator|.
name|write
argument_list|(
name|ch
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
