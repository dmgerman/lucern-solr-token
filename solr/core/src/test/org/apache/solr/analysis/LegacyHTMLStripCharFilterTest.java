begin_unit
begin_package
DECL|package|org.apache.solr.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analysis
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
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
name|io
operator|.
name|InputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Analyzer
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|BaseTokenStreamTestCase
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|CharReader
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|MockTokenizer
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Tokenizer
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|_TestUtil
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
import|;
end_import
begin_class
DECL|class|LegacyHTMLStripCharFilterTest
specifier|public
class|class
name|LegacyHTMLStripCharFilterTest
extends|extends
name|BaseTokenStreamTestCase
block|{
comment|//this is some text  here is a  link  and another  link . This is an entity:& plus a<.  Here is an&
comment|//
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|html
init|=
literal|"<div class=\"foo\">this is some text</div> here is a<a href=\"#bar\">link</a> and "
operator|+
literal|"another<a href=\"http://lucene.apache.org/\">link</a>. "
operator|+
literal|"This is an entity:&amp; plus a&lt;.  Here is an&.<!-- is a comment -->"
decl_stmt|;
name|String
name|gold
init|=
literal|" this is some text  here is a  link  and "
operator|+
literal|"another  link . "
operator|+
literal|"This is an entity:& plus a<.  Here is an&.  "
decl_stmt|;
name|LegacyHTMLStripCharFilter
name|reader
init|=
operator|new
name|LegacyHTMLStripCharFilter
argument_list|(
name|CharReader
operator|.
name|get
argument_list|(
operator|new
name|StringReader
argument_list|(
name|html
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|ch
init|=
operator|-
literal|1
decl_stmt|;
name|char
index|[]
name|goldArray
init|=
name|gold
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|int
name|position
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|ch
operator|=
name|reader
operator|.
name|read
argument_list|()
operator|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|char
name|theChar
init|=
operator|(
name|char
operator|)
name|ch
decl_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|theChar
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"\""
operator|+
name|theChar
operator|+
literal|"\""
operator|+
literal|" at position: "
operator|+
name|position
operator|+
literal|" does not equal: "
operator|+
name|goldArray
index|[
name|position
index|]
operator|+
literal|" Buffer so far: "
operator|+
name|builder
operator|+
literal|"<EOB>"
argument_list|,
name|theChar
operator|==
name|goldArray
index|[
name|position
index|]
argument_list|)
expr_stmt|;
name|position
operator|++
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|gold
argument_list|,
name|builder
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//Some sanity checks, but not a full-fledged check
DECL|method|testHTML
specifier|public
name|void
name|testHTML
parameter_list|()
throws|throws
name|Exception
block|{
name|InputStream
name|stream
init|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"htmlStripReaderTest.html"
argument_list|)
decl_stmt|;
name|LegacyHTMLStripCharFilter
name|reader
init|=
operator|new
name|LegacyHTMLStripCharFilter
argument_list|(
name|CharReader
operator|.
name|get
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|stream
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|ch
init|=
operator|-
literal|1
decl_stmt|;
while|while
condition|(
operator|(
name|ch
operator|=
name|reader
operator|.
name|read
argument_list|()
operator|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
operator|(
name|char
operator|)
name|ch
argument_list|)
expr_stmt|;
block|}
name|String
name|str
init|=
name|builder
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Entity not properly escaped"
argument_list|,
name|str
operator|.
name|indexOf
argument_list|(
literal|"&lt;"
argument_list|)
operator|==
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|//there is one> in the text
name|assertTrue
argument_list|(
literal|"Forrest should have been stripped out"
argument_list|,
name|str
operator|.
name|indexOf
argument_list|(
literal|"forrest"
argument_list|)
operator|==
operator|-
literal|1
operator|&&
name|str
operator|.
name|indexOf
argument_list|(
literal|"Forrest"
argument_list|)
operator|==
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"File should start with 'Welcome to Solr' after trimming"
argument_list|,
name|str
operator|.
name|trim
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"Welcome to Solr"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"File should start with 'Foundation.' after trimming"
argument_list|,
name|str
operator|.
name|trim
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|"Foundation."
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testGamma
specifier|public
name|void
name|testGamma
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|test
init|=
literal|"&Gamma;"
decl_stmt|;
name|String
name|gold
init|=
literal|"\u0393"
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|set
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|set
operator|.
name|add
argument_list|(
literal|"reserved"
argument_list|)
expr_stmt|;
name|Reader
name|reader
init|=
operator|new
name|LegacyHTMLStripCharFilter
argument_list|(
name|CharReader
operator|.
name|get
argument_list|(
operator|new
name|StringReader
argument_list|(
name|test
argument_list|)
argument_list|)
argument_list|,
name|set
argument_list|)
decl_stmt|;
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|ch
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|ch
operator|=
name|reader
operator|.
name|read
argument_list|()
operator|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
operator|(
name|char
operator|)
name|ch
argument_list|)
expr_stmt|;
block|}
name|String
name|result
init|=
name|builder
operator|.
name|toString
argument_list|()
decl_stmt|;
comment|// System.out.println("Resu: " + result + "<EOL>");
comment|// System.out.println("Gold: " + gold + "<EOL>");
name|assertTrue
argument_list|(
name|result
operator|+
literal|" is not equal to "
operator|+
name|gold
operator|+
literal|"<EOS>"
argument_list|,
name|result
operator|.
name|equals
argument_list|(
name|gold
argument_list|)
operator|==
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|testEntities
specifier|public
name|void
name|testEntities
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|test
init|=
literal|"&nbsp;&lt;foo&gt;&Uuml;bermensch&#61;&Gamma; bar&#x393;"
decl_stmt|;
name|String
name|gold
init|=
literal|"<foo> \u00DCbermensch = \u0393 bar \u0393"
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|set
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|set
operator|.
name|add
argument_list|(
literal|"reserved"
argument_list|)
expr_stmt|;
name|Reader
name|reader
init|=
operator|new
name|LegacyHTMLStripCharFilter
argument_list|(
name|CharReader
operator|.
name|get
argument_list|(
operator|new
name|StringReader
argument_list|(
name|test
argument_list|)
argument_list|)
argument_list|,
name|set
argument_list|)
decl_stmt|;
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|ch
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|ch
operator|=
name|reader
operator|.
name|read
argument_list|()
operator|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
operator|(
name|char
operator|)
name|ch
argument_list|)
expr_stmt|;
block|}
name|String
name|result
init|=
name|builder
operator|.
name|toString
argument_list|()
decl_stmt|;
comment|// System.out.println("Resu: " + result + "<EOL>");
comment|// System.out.println("Gold: " + gold + "<EOL>");
name|assertTrue
argument_list|(
name|result
operator|+
literal|" is not equal to "
operator|+
name|gold
operator|+
literal|"<EOS>"
argument_list|,
name|result
operator|.
name|equals
argument_list|(
name|gold
argument_list|)
operator|==
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|testMoreEntities
specifier|public
name|void
name|testMoreEntities
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|test
init|=
literal|"&nbsp;&lt;junk/&gt;&nbsp;&#33;&#64; and&#8217;"
decl_stmt|;
name|String
name|gold
init|=
literal|"<junk/>   ! @ and â"
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|set
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|set
operator|.
name|add
argument_list|(
literal|"reserved"
argument_list|)
expr_stmt|;
name|Reader
name|reader
init|=
operator|new
name|LegacyHTMLStripCharFilter
argument_list|(
name|CharReader
operator|.
name|get
argument_list|(
operator|new
name|StringReader
argument_list|(
name|test
argument_list|)
argument_list|)
argument_list|,
name|set
argument_list|)
decl_stmt|;
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|ch
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|ch
operator|=
name|reader
operator|.
name|read
argument_list|()
operator|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
operator|(
name|char
operator|)
name|ch
argument_list|)
expr_stmt|;
block|}
name|String
name|result
init|=
name|builder
operator|.
name|toString
argument_list|()
decl_stmt|;
comment|// System.out.println("Resu: " + result + "<EOL>");
comment|// System.out.println("Gold: " + gold + "<EOL>");
name|assertTrue
argument_list|(
name|result
operator|+
literal|" is not equal to "
operator|+
name|gold
argument_list|,
name|result
operator|.
name|equals
argument_list|(
name|gold
argument_list|)
operator|==
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|testReserved
specifier|public
name|void
name|testReserved
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|test
init|=
literal|"aaa bbb<reserved ccc=\"ddddd\"> eeee</reserved> ffff<reserved ggg=\"hhhh\"/><other/>"
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|set
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|set
operator|.
name|add
argument_list|(
literal|"reserved"
argument_list|)
expr_stmt|;
name|Reader
name|reader
init|=
operator|new
name|LegacyHTMLStripCharFilter
argument_list|(
name|CharReader
operator|.
name|get
argument_list|(
operator|new
name|StringReader
argument_list|(
name|test
argument_list|)
argument_list|)
argument_list|,
name|set
argument_list|)
decl_stmt|;
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|ch
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|ch
operator|=
name|reader
operator|.
name|read
argument_list|()
operator|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
operator|(
name|char
operator|)
name|ch
argument_list|)
expr_stmt|;
block|}
name|String
name|result
init|=
name|builder
operator|.
name|toString
argument_list|()
decl_stmt|;
comment|// System.out.println("Result: " + result);
name|assertTrue
argument_list|(
literal|"Escaped tag not preserved: "
operator|+
name|result
operator|.
name|indexOf
argument_list|(
literal|"reserved"
argument_list|)
argument_list|,
name|result
operator|.
name|indexOf
argument_list|(
literal|"reserved"
argument_list|)
operator|==
literal|9
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Escaped tag not preserved: "
operator|+
name|result
operator|.
name|indexOf
argument_list|(
literal|"reserved"
argument_list|,
literal|15
argument_list|)
argument_list|,
name|result
operator|.
name|indexOf
argument_list|(
literal|"reserved"
argument_list|,
literal|15
argument_list|)
operator|==
literal|38
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Escaped tag not preserved: "
operator|+
name|result
operator|.
name|indexOf
argument_list|(
literal|"reserved"
argument_list|,
literal|41
argument_list|)
argument_list|,
name|result
operator|.
name|indexOf
argument_list|(
literal|"reserved"
argument_list|,
literal|41
argument_list|)
operator|==
literal|54
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Other tag should be removed"
argument_list|,
name|result
operator|.
name|indexOf
argument_list|(
literal|"other"
argument_list|)
operator|==
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|testMalformedHTML
specifier|public
name|void
name|testMalformedHTML
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|test
init|=
literal|"a<a hr<ef=aa<a>></close</a>"
decl_stmt|;
name|String
name|gold
init|=
literal|"a<a hr<ef=aa></close "
decl_stmt|;
name|Reader
name|reader
init|=
operator|new
name|LegacyHTMLStripCharFilter
argument_list|(
name|CharReader
operator|.
name|get
argument_list|(
operator|new
name|StringReader
argument_list|(
name|test
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|ch
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|ch
operator|=
name|reader
operator|.
name|read
argument_list|()
operator|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
operator|(
name|char
operator|)
name|ch
argument_list|)
expr_stmt|;
block|}
name|String
name|result
init|=
name|builder
operator|.
name|toString
argument_list|()
decl_stmt|;
comment|// System.out.println("Resu: " + result + "<EOL>");
comment|// System.out.println("Gold: " + gold + "<EOL>");
name|assertTrue
argument_list|(
name|result
operator|+
literal|" is not equal to "
operator|+
name|gold
operator|+
literal|"<EOS>"
argument_list|,
name|result
operator|.
name|equals
argument_list|(
name|gold
argument_list|)
operator|==
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|testBufferOverflow
specifier|public
name|void
name|testBufferOverflow
parameter_list|()
throws|throws
name|Exception
block|{
name|StringBuilder
name|testBuilder
init|=
operator|new
name|StringBuilder
argument_list|(
name|LegacyHTMLStripCharFilter
operator|.
name|DEFAULT_READ_AHEAD
operator|+
literal|50
argument_list|)
decl_stmt|;
name|testBuilder
operator|.
name|append
argument_list|(
literal|"ah<?> ??????"
argument_list|)
expr_stmt|;
name|appendChars
argument_list|(
name|testBuilder
argument_list|,
name|LegacyHTMLStripCharFilter
operator|.
name|DEFAULT_READ_AHEAD
operator|+
literal|500
argument_list|)
expr_stmt|;
name|processBuffer
argument_list|(
name|testBuilder
operator|.
name|toString
argument_list|()
argument_list|,
literal|"Failed on pseudo proc. instr."
argument_list|)
expr_stmt|;
comment|//processing instructions
name|testBuilder
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|testBuilder
operator|.
name|append
argument_list|(
literal|"<!--"
argument_list|)
expr_stmt|;
comment|//comments
name|appendChars
argument_list|(
name|testBuilder
argument_list|,
literal|3
operator|*
name|LegacyHTMLStripCharFilter
operator|.
name|DEFAULT_READ_AHEAD
operator|+
literal|500
argument_list|)
expr_stmt|;
comment|//comments have two lookaheads
name|testBuilder
operator|.
name|append
argument_list|(
literal|"-->foo"
argument_list|)
expr_stmt|;
name|processBuffer
argument_list|(
name|testBuilder
operator|.
name|toString
argument_list|()
argument_list|,
literal|"Failed w/ comment"
argument_list|)
expr_stmt|;
name|testBuilder
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|testBuilder
operator|.
name|append
argument_list|(
literal|"<?"
argument_list|)
expr_stmt|;
name|appendChars
argument_list|(
name|testBuilder
argument_list|,
name|LegacyHTMLStripCharFilter
operator|.
name|DEFAULT_READ_AHEAD
operator|+
literal|500
argument_list|)
expr_stmt|;
name|testBuilder
operator|.
name|append
argument_list|(
literal|"?>"
argument_list|)
expr_stmt|;
name|processBuffer
argument_list|(
name|testBuilder
operator|.
name|toString
argument_list|()
argument_list|,
literal|"Failed with proc. instr."
argument_list|)
expr_stmt|;
name|testBuilder
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|testBuilder
operator|.
name|append
argument_list|(
literal|"<b "
argument_list|)
expr_stmt|;
name|appendChars
argument_list|(
name|testBuilder
argument_list|,
name|LegacyHTMLStripCharFilter
operator|.
name|DEFAULT_READ_AHEAD
operator|+
literal|500
argument_list|)
expr_stmt|;
name|testBuilder
operator|.
name|append
argument_list|(
literal|"/>"
argument_list|)
expr_stmt|;
name|processBuffer
argument_list|(
name|testBuilder
operator|.
name|toString
argument_list|()
argument_list|,
literal|"Failed on tag"
argument_list|)
expr_stmt|;
block|}
DECL|method|appendChars
specifier|private
name|void
name|appendChars
parameter_list|(
name|StringBuilder
name|testBuilder
parameter_list|,
name|int
name|numChars
parameter_list|)
block|{
name|int
name|i1
init|=
name|numChars
operator|/
literal|2
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|i1
condition|;
name|i
operator|++
control|)
block|{
name|testBuilder
operator|.
name|append
argument_list|(
literal|'a'
argument_list|)
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
comment|//tack on enough to go beyond the mark readahead limit, since<?> makes LegacyHTMLStripCharFilter think it is a processing instruction
block|}
block|}
DECL|method|processBuffer
specifier|private
name|void
name|processBuffer
parameter_list|(
name|String
name|test
parameter_list|,
name|String
name|assertMsg
parameter_list|)
throws|throws
name|IOException
block|{
comment|// System.out.println("-------------------processBuffer----------");
name|Reader
name|reader
init|=
operator|new
name|LegacyHTMLStripCharFilter
argument_list|(
name|CharReader
operator|.
name|get
argument_list|(
operator|new
name|BufferedReader
argument_list|(
operator|new
name|StringReader
argument_list|(
name|test
argument_list|)
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
comment|//force the use of BufferedReader
name|int
name|ch
init|=
literal|0
decl_stmt|;
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
try|try
block|{
while|while
condition|(
operator|(
name|ch
operator|=
name|reader
operator|.
name|read
argument_list|()
operator|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
operator|(
name|char
operator|)
name|ch
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
comment|// System.out.println("String (trimmed): " + builder.toString().trim() + "<EOS>");
block|}
name|assertTrue
argument_list|(
name|assertMsg
operator|+
literal|"::: "
operator|+
name|builder
operator|.
name|toString
argument_list|()
operator|+
literal|" is not equal to "
operator|+
name|test
argument_list|,
name|builder
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
name|test
argument_list|)
operator|==
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|testComment
specifier|public
name|void
name|testComment
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|test
init|=
literal|"<!--- three dashes, still a valid comment ---> "
decl_stmt|;
name|String
name|gold
init|=
literal|"  "
decl_stmt|;
name|Reader
name|reader
init|=
operator|new
name|LegacyHTMLStripCharFilter
argument_list|(
name|CharReader
operator|.
name|get
argument_list|(
operator|new
name|BufferedReader
argument_list|(
operator|new
name|StringReader
argument_list|(
name|test
argument_list|)
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
comment|//force the use of BufferedReader
name|int
name|ch
init|=
literal|0
decl_stmt|;
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
try|try
block|{
while|while
condition|(
operator|(
name|ch
operator|=
name|reader
operator|.
name|read
argument_list|()
operator|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
operator|(
name|char
operator|)
name|ch
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
comment|// System.out.println("String: " + builder.toString());
block|}
name|assertTrue
argument_list|(
name|builder
operator|.
name|toString
argument_list|()
operator|+
literal|" is not equal to "
operator|+
name|gold
operator|+
literal|"<EOS>"
argument_list|,
name|builder
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
name|gold
argument_list|)
operator|==
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|doTestOffsets
specifier|public
name|void
name|doTestOffsets
parameter_list|(
name|String
name|in
parameter_list|)
throws|throws
name|Exception
block|{
name|LegacyHTMLStripCharFilter
name|reader
init|=
operator|new
name|LegacyHTMLStripCharFilter
argument_list|(
name|CharReader
operator|.
name|get
argument_list|(
operator|new
name|BufferedReader
argument_list|(
operator|new
name|StringReader
argument_list|(
name|in
argument_list|)
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|ch
init|=
literal|0
decl_stmt|;
name|int
name|off
init|=
literal|0
decl_stmt|;
comment|// offset in the reader
name|int
name|strOff
init|=
operator|-
literal|1
decl_stmt|;
comment|// offset in the original string
while|while
condition|(
operator|(
name|ch
operator|=
name|reader
operator|.
name|read
argument_list|()
operator|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|int
name|correctedOff
init|=
name|reader
operator|.
name|correctOffset
argument_list|(
name|off
argument_list|)
decl_stmt|;
if|if
condition|(
name|ch
operator|==
literal|'X'
condition|)
block|{
name|strOff
operator|=
name|in
operator|.
name|indexOf
argument_list|(
literal|'X'
argument_list|,
name|strOff
operator|+
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|strOff
argument_list|,
name|correctedOff
argument_list|)
expr_stmt|;
block|}
name|off
operator|++
expr_stmt|;
block|}
block|}
DECL|method|testOffsets
specifier|public
name|void
name|testOffsets
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestOffsets
argument_list|(
literal|"hello X how X are you"
argument_list|)
expr_stmt|;
name|doTestOffsets
argument_list|(
literal|"hello<p> X<p> how<p>X are you"
argument_list|)
expr_stmt|;
name|doTestOffsets
argument_list|(
literal|"X&amp; X&#40; X&lt;&gt; X"
argument_list|)
expr_stmt|;
comment|// test backtracking
name|doTestOffsets
argument_list|(
literal|"X<&zz>X&#< X><&l>&g< X"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
argument_list|(
literal|"broken offsets: see LUCENE-2208"
argument_list|)
DECL|method|testRandom
specifier|public
name|void
name|testRandom
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|analyzer
init|=
operator|new
name|Analyzer
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
name|Tokenizer
name|tokenizer
init|=
operator|new
name|MockTokenizer
argument_list|(
name|reader
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
name|tokenizer
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Reader
name|initReader
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
return|return
operator|new
name|LegacyHTMLStripCharFilter
argument_list|(
name|CharReader
operator|.
name|get
argument_list|(
operator|new
name|BufferedReader
argument_list|(
name|reader
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|int
name|numRounds
init|=
name|RANDOM_MULTIPLIER
operator|*
literal|10000
decl_stmt|;
name|checkRandomData
argument_list|(
name|random
argument_list|()
argument_list|,
name|analyzer
argument_list|,
name|numRounds
argument_list|)
expr_stmt|;
block|}
DECL|method|testRandomBrokenHTML
specifier|public
name|void
name|testRandomBrokenHTML
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|maxNumElements
init|=
literal|10000
decl_stmt|;
name|String
name|text
init|=
name|_TestUtil
operator|.
name|randomHtmlishString
argument_list|(
name|random
argument_list|()
argument_list|,
name|maxNumElements
argument_list|)
decl_stmt|;
name|Reader
name|reader
init|=
operator|new
name|LegacyHTMLStripCharFilter
argument_list|(
name|CharReader
operator|.
name|get
argument_list|(
operator|new
name|StringReader
argument_list|(
name|text
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
while|while
condition|(
name|reader
operator|.
name|read
argument_list|()
operator|!=
operator|-
literal|1
condition|)
empty_stmt|;
block|}
DECL|method|testRandomText
specifier|public
name|void
name|testRandomText
parameter_list|()
throws|throws
name|Exception
block|{
name|StringBuilder
name|text
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|minNumWords
init|=
literal|10
decl_stmt|;
name|int
name|maxNumWords
init|=
literal|10000
decl_stmt|;
name|int
name|minWordLength
init|=
literal|3
decl_stmt|;
name|int
name|maxWordLength
init|=
literal|20
decl_stmt|;
name|int
name|numWords
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
name|minNumWords
argument_list|,
name|maxNumWords
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|4
argument_list|)
condition|)
block|{
case|case
literal|0
case|:
block|{
for|for
control|(
name|int
name|wordNum
init|=
literal|0
init|;
name|wordNum
operator|<
name|numWords
condition|;
operator|++
name|wordNum
control|)
block|{
name|text
operator|.
name|append
argument_list|(
name|_TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|,
name|maxWordLength
argument_list|)
argument_list|)
expr_stmt|;
name|text
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
case|case
literal|1
case|:
block|{
for|for
control|(
name|int
name|wordNum
init|=
literal|0
init|;
name|wordNum
operator|<
name|numWords
condition|;
operator|++
name|wordNum
control|)
block|{
name|text
operator|.
name|append
argument_list|(
name|_TestUtil
operator|.
name|randomRealisticUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|,
name|minWordLength
argument_list|,
name|maxWordLength
argument_list|)
argument_list|)
expr_stmt|;
name|text
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
default|default:
block|{
comment|// ASCII 50% of the time
for|for
control|(
name|int
name|wordNum
init|=
literal|0
init|;
name|wordNum
operator|<
name|numWords
condition|;
operator|++
name|wordNum
control|)
block|{
name|text
operator|.
name|append
argument_list|(
name|_TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|text
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|Reader
name|reader
init|=
operator|new
name|LegacyHTMLStripCharFilter
argument_list|(
name|CharReader
operator|.
name|get
argument_list|(
operator|new
name|StringReader
argument_list|(
name|text
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
while|while
condition|(
name|reader
operator|.
name|read
argument_list|()
operator|!=
operator|-
literal|1
condition|)
empty_stmt|;
block|}
block|}
end_class
end_unit
