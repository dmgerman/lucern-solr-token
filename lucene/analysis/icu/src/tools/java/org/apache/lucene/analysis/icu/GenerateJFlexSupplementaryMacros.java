begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.icu
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|icu
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|text
operator|.
name|DateFormat
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TimeZone
import|;
end_import
begin_import
import|import
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|text
operator|.
name|UnicodeSet
import|;
end_import
begin_import
import|import
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|text
operator|.
name|UnicodeSetIterator
import|;
end_import
begin_import
import|import
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|util
operator|.
name|VersionInfo
import|;
end_import
begin_comment
comment|/** creates a macro to augment jflex's unicode wordbreak support for> BMP */
end_comment
begin_class
DECL|class|GenerateJFlexSupplementaryMacros
specifier|public
class|class
name|GenerateJFlexSupplementaryMacros
block|{
DECL|field|BMP
specifier|private
specifier|static
specifier|final
name|UnicodeSet
name|BMP
init|=
operator|new
name|UnicodeSet
argument_list|(
literal|"[\u0000-\uFFFF]"
argument_list|)
decl_stmt|;
DECL|field|NL
specifier|private
specifier|static
specifier|final
name|String
name|NL
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
decl_stmt|;
DECL|field|DATE_FORMAT
specifier|private
specifier|static
specifier|final
name|DateFormat
name|DATE_FORMAT
init|=
name|DateFormat
operator|.
name|getDateTimeInstance
argument_list|(
name|DateFormat
operator|.
name|FULL
argument_list|,
name|DateFormat
operator|.
name|FULL
argument_list|,
name|Locale
operator|.
name|US
argument_list|)
decl_stmt|;
static|static
block|{
name|DATE_FORMAT
operator|.
name|setTimeZone
argument_list|(
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"UTC"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|field|APACHE_LICENSE
specifier|private
specifier|static
specifier|final
name|String
name|APACHE_LICENSE
init|=
literal|"/*"
operator|+
name|NL
operator|+
literal|" * Copyright 2010 The Apache Software Foundation."
operator|+
name|NL
operator|+
literal|" *"
operator|+
name|NL
operator|+
literal|" * Licensed under the Apache License, Version 2.0 (the \"License\");"
operator|+
name|NL
operator|+
literal|" * you may not use this file except in compliance with the License."
operator|+
name|NL
operator|+
literal|" * You may obtain a copy of the License at"
operator|+
name|NL
operator|+
literal|" *"
operator|+
name|NL
operator|+
literal|" *      http://www.apache.org/licenses/LICENSE-2.0"
operator|+
name|NL
operator|+
literal|" *"
operator|+
name|NL
operator|+
literal|" * Unless required by applicable law or agreed to in writing, software"
operator|+
name|NL
operator|+
literal|" * distributed under the License is distributed on an \"AS IS\" BASIS,"
operator|+
name|NL
operator|+
literal|" * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied."
operator|+
name|NL
operator|+
literal|" * See the License for the specific language governing permissions and"
operator|+
name|NL
operator|+
literal|" * limitations under the License."
operator|+
name|NL
operator|+
literal|" */"
operator|+
name|NL
operator|+
name|NL
decl_stmt|;
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
name|args
index|[]
parameter_list|)
block|{
name|outputHeader
argument_list|()
expr_stmt|;
name|outputMacro
argument_list|(
literal|"ALetterSupp"
argument_list|,
literal|"[:WordBreak=ALetter:]"
argument_list|)
expr_stmt|;
name|outputMacro
argument_list|(
literal|"FormatSupp"
argument_list|,
literal|"[:WordBreak=Format:]"
argument_list|)
expr_stmt|;
name|outputMacro
argument_list|(
literal|"ExtendSupp"
argument_list|,
literal|"[:WordBreak=Extend:]"
argument_list|)
expr_stmt|;
name|outputMacro
argument_list|(
literal|"NumericSupp"
argument_list|,
literal|"[:WordBreak=Numeric:]"
argument_list|)
expr_stmt|;
name|outputMacro
argument_list|(
literal|"KatakanaSupp"
argument_list|,
literal|"[:WordBreak=Katakana:]"
argument_list|)
expr_stmt|;
name|outputMacro
argument_list|(
literal|"MidLetterSupp"
argument_list|,
literal|"[:WordBreak=MidLetter:]"
argument_list|)
expr_stmt|;
name|outputMacro
argument_list|(
literal|"MidNumSupp"
argument_list|,
literal|"[:WordBreak=MidNum:]"
argument_list|)
expr_stmt|;
name|outputMacro
argument_list|(
literal|"MidNumLetSupp"
argument_list|,
literal|"[:WordBreak=MidNumLet:]"
argument_list|)
expr_stmt|;
name|outputMacro
argument_list|(
literal|"ExtendNumLetSupp"
argument_list|,
literal|"[:WordBreak=ExtendNumLet:]"
argument_list|)
expr_stmt|;
name|outputMacro
argument_list|(
literal|"ExtendNumLetSupp"
argument_list|,
literal|"[:WordBreak=ExtendNumLet:]"
argument_list|)
expr_stmt|;
name|outputMacro
argument_list|(
literal|"ComplexContextSupp"
argument_list|,
literal|"[:LineBreak=Complex_Context:]"
argument_list|)
expr_stmt|;
name|outputMacro
argument_list|(
literal|"HanSupp"
argument_list|,
literal|"[:Script=Han:]"
argument_list|)
expr_stmt|;
name|outputMacro
argument_list|(
literal|"HiraganaSupp"
argument_list|,
literal|"[:Script=Hiragana:]"
argument_list|)
expr_stmt|;
block|}
DECL|method|outputHeader
specifier|static
name|void
name|outputHeader
parameter_list|()
block|{
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
name|APACHE_LICENSE
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"// Generated using ICU4J "
operator|+
name|VersionInfo
operator|.
name|ICU_VERSION
operator|.
name|toString
argument_list|()
operator|+
literal|" on "
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|DATE_FORMAT
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"// by "
operator|+
name|GenerateJFlexSupplementaryMacros
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
name|NL
operator|+
name|NL
argument_list|)
expr_stmt|;
block|}
comment|// we have to carefully output the possibilities as compact utf-16
comment|// range expressions, or jflex will OOM!
DECL|method|outputMacro
specifier|static
name|void
name|outputMacro
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|pattern
parameter_list|)
block|{
name|UnicodeSet
name|set
init|=
operator|new
name|UnicodeSet
argument_list|(
name|pattern
argument_list|)
decl_stmt|;
name|set
operator|.
name|removeAll
argument_list|(
name|BMP
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|name
operator|+
literal|" = ("
argument_list|)
expr_stmt|;
comment|// if the set is empty, we have to do this or jflex will barf
if|if
condition|(
name|set
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\t  []"
argument_list|)
expr_stmt|;
block|}
name|HashMap
argument_list|<
name|Character
argument_list|,
name|UnicodeSet
argument_list|>
name|utf16ByLead
init|=
operator|new
name|HashMap
argument_list|<
name|Character
argument_list|,
name|UnicodeSet
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|UnicodeSetIterator
name|it
init|=
operator|new
name|UnicodeSetIterator
argument_list|(
name|set
argument_list|)
init|;
name|it
operator|.
name|next
argument_list|()
condition|;
control|)
block|{
name|char
name|utf16
index|[]
init|=
name|Character
operator|.
name|toChars
argument_list|(
name|it
operator|.
name|codepoint
argument_list|)
decl_stmt|;
name|UnicodeSet
name|trails
init|=
name|utf16ByLead
operator|.
name|get
argument_list|(
name|utf16
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|trails
operator|==
literal|null
condition|)
block|{
name|trails
operator|=
operator|new
name|UnicodeSet
argument_list|()
expr_stmt|;
name|utf16ByLead
operator|.
name|put
argument_list|(
name|utf16
index|[
literal|0
index|]
argument_list|,
name|trails
argument_list|)
expr_stmt|;
block|}
name|trails
operator|.
name|add
argument_list|(
name|utf16
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
name|boolean
name|isFirst
init|=
literal|true
decl_stmt|;
for|for
control|(
name|Character
name|c
range|:
name|utf16ByLead
operator|.
name|keySet
argument_list|()
control|)
block|{
name|UnicodeSet
name|trail
init|=
name|utf16ByLead
operator|.
name|get
argument_list|(
name|c
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
name|isFirst
condition|?
literal|"\t  "
else|:
literal|"\t| "
argument_list|)
expr_stmt|;
name|isFirst
operator|=
literal|false
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"([\\u"
operator|+
name|Integer
operator|.
name|toHexString
argument_list|(
name|c
argument_list|)
operator|+
literal|"]"
operator|+
name|trail
operator|.
name|getRegexEquivalent
argument_list|()
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
