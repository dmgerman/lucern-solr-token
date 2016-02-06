begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.benchmark.byTask.feeds
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|byTask
operator|.
name|feeds
package|;
end_package
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
name|Arrays
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|HashSet
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
name|Properties
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
name|cyberneko
operator|.
name|html
operator|.
name|parsers
operator|.
name|SAXParser
import|;
end_import
begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|Attributes
import|;
end_import
begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|InputSource
import|;
end_import
begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import
begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|helpers
operator|.
name|DefaultHandler
import|;
end_import
begin_comment
comment|/**  * Simple HTML Parser extracting title, meta tags, and body text  * that is based on<a href="http://nekohtml.sourceforge.net/">NekoHTML</a>.  */
end_comment
begin_class
DECL|class|DemoHTMLParser
specifier|public
class|class
name|DemoHTMLParser
implements|implements
name|HTMLParser
block|{
comment|/** The actual parser to read HTML documents */
DECL|class|Parser
specifier|public
specifier|static
specifier|final
class|class
name|Parser
block|{
DECL|field|metaTags
specifier|public
specifier|final
name|Properties
name|metaTags
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
DECL|field|title
DECL|field|body
specifier|public
specifier|final
name|String
name|title
decl_stmt|,
name|body
decl_stmt|;
DECL|method|Parser
specifier|public
name|Parser
parameter_list|(
name|Reader
name|reader
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
block|{
name|this
argument_list|(
operator|new
name|InputSource
argument_list|(
name|reader
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|Parser
specifier|public
name|Parser
parameter_list|(
name|InputSource
name|source
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
block|{
specifier|final
name|SAXParser
name|parser
init|=
operator|new
name|SAXParser
argument_list|()
decl_stmt|;
name|parser
operator|.
name|setFeature
argument_list|(
literal|"http://xml.org/sax/features/namespaces"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|parser
operator|.
name|setFeature
argument_list|(
literal|"http://cyberneko.org/html/features/balance-tags"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|parser
operator|.
name|setFeature
argument_list|(
literal|"http://cyberneko.org/html/features/report-errors"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|parser
operator|.
name|setProperty
argument_list|(
literal|"http://cyberneko.org/html/properties/names/elems"
argument_list|,
literal|"lower"
argument_list|)
expr_stmt|;
name|parser
operator|.
name|setProperty
argument_list|(
literal|"http://cyberneko.org/html/properties/names/attrs"
argument_list|,
literal|"lower"
argument_list|)
expr_stmt|;
specifier|final
name|StringBuilder
name|title
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|,
name|body
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
specifier|final
name|DefaultHandler
name|handler
init|=
operator|new
name|DefaultHandler
argument_list|()
block|{
specifier|private
name|int
name|inBODY
init|=
literal|0
decl_stmt|,
name|inHEAD
init|=
literal|0
decl_stmt|,
name|inTITLE
init|=
literal|0
decl_stmt|,
name|suppressed
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|startElement
parameter_list|(
name|String
name|namespaceURI
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|qName
parameter_list|,
name|Attributes
name|atts
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|inHEAD
operator|>
literal|0
condition|)
block|{
if|if
condition|(
literal|"title"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|inTITLE
operator|++
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
literal|"meta"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|String
name|name
init|=
name|atts
operator|.
name|getValue
argument_list|(
literal|"name"
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
name|name
operator|=
name|atts
operator|.
name|getValue
argument_list|(
literal|"http-equiv"
argument_list|)
expr_stmt|;
block|}
specifier|final
name|String
name|val
init|=
name|atts
operator|.
name|getValue
argument_list|(
literal|"content"
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|!=
literal|null
operator|&&
name|val
operator|!=
literal|null
condition|)
block|{
name|metaTags
operator|.
name|setProperty
argument_list|(
name|name
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|inBODY
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|SUPPRESS_ELEMENTS
operator|.
name|contains
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|suppressed
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"img"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
comment|// the original javacc-based parser preserved<IMG alt="..."/>
comment|// attribute as body text in [] parenthesis:
specifier|final
name|String
name|alt
init|=
name|atts
operator|.
name|getValue
argument_list|(
literal|"alt"
argument_list|)
decl_stmt|;
if|if
condition|(
name|alt
operator|!=
literal|null
condition|)
block|{
name|body
operator|.
name|append
argument_list|(
literal|'['
argument_list|)
operator|.
name|append
argument_list|(
name|alt
argument_list|)
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
expr_stmt|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
literal|"body"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|inBODY
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"head"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|inHEAD
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"frameset"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
literal|"This parser does not support HTML framesets."
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|endElement
parameter_list|(
name|String
name|namespaceURI
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|qName
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|inBODY
operator|>
literal|0
condition|)
block|{
if|if
condition|(
literal|"body"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|inBODY
operator|--
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|ENDLINE_ELEMENTS
operator|.
name|contains
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|body
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|SUPPRESS_ELEMENTS
operator|.
name|contains
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|suppressed
operator|--
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|inHEAD
operator|>
literal|0
condition|)
block|{
if|if
condition|(
literal|"head"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|inHEAD
operator|--
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|inTITLE
operator|>
literal|0
operator|&&
literal|"title"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|inTITLE
operator|--
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|characters
parameter_list|(
name|char
index|[]
name|ch
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|inBODY
operator|>
literal|0
operator|&&
name|suppressed
operator|==
literal|0
condition|)
block|{
name|body
operator|.
name|append
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|inTITLE
operator|>
literal|0
condition|)
block|{
name|title
operator|.
name|append
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|InputSource
name|resolveEntity
parameter_list|(
name|String
name|publicId
parameter_list|,
name|String
name|systemId
parameter_list|)
block|{
comment|// disable network access caused by DTDs
return|return
operator|new
name|InputSource
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|""
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|parser
operator|.
name|setContentHandler
argument_list|(
name|handler
argument_list|)
expr_stmt|;
name|parser
operator|.
name|setErrorHandler
argument_list|(
name|handler
argument_list|)
expr_stmt|;
name|parser
operator|.
name|parse
argument_list|(
name|source
argument_list|)
expr_stmt|;
comment|// the javacc-based parser trimmed title (which should be done for HTML in all cases):
name|this
operator|.
name|title
operator|=
name|title
operator|.
name|toString
argument_list|()
operator|.
name|trim
argument_list|()
expr_stmt|;
comment|// assign body text
name|this
operator|.
name|body
operator|=
name|body
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
DECL|method|createElementNameSet
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|createElementNameSet
parameter_list|(
name|String
modifier|...
name|names
parameter_list|)
block|{
return|return
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|names
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
comment|/** HTML elements that cause a line break (they are block-elements) */
DECL|field|ENDLINE_ELEMENTS
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|ENDLINE_ELEMENTS
init|=
name|createElementNameSet
argument_list|(
literal|"p"
argument_list|,
literal|"h1"
argument_list|,
literal|"h2"
argument_list|,
literal|"h3"
argument_list|,
literal|"h4"
argument_list|,
literal|"h5"
argument_list|,
literal|"h6"
argument_list|,
literal|"div"
argument_list|,
literal|"ul"
argument_list|,
literal|"ol"
argument_list|,
literal|"dl"
argument_list|,
literal|"pre"
argument_list|,
literal|"hr"
argument_list|,
literal|"blockquote"
argument_list|,
literal|"address"
argument_list|,
literal|"fieldset"
argument_list|,
literal|"table"
argument_list|,
literal|"form"
argument_list|,
literal|"noscript"
argument_list|,
literal|"li"
argument_list|,
literal|"dt"
argument_list|,
literal|"dd"
argument_list|,
literal|"noframes"
argument_list|,
literal|"br"
argument_list|,
literal|"tr"
argument_list|,
literal|"select"
argument_list|,
literal|"option"
argument_list|)
decl_stmt|;
comment|/** HTML elements with contents that are ignored */
DECL|field|SUPPRESS_ELEMENTS
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|SUPPRESS_ELEMENTS
init|=
name|createElementNameSet
argument_list|(
literal|"style"
argument_list|,
literal|"script"
argument_list|)
decl_stmt|;
block|}
annotation|@
name|Override
DECL|method|parse
specifier|public
name|DocData
name|parse
parameter_list|(
name|DocData
name|docData
parameter_list|,
name|String
name|name
parameter_list|,
name|Date
name|date
parameter_list|,
name|Reader
name|reader
parameter_list|,
name|TrecContentSource
name|trecSrc
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
return|return
name|parse
argument_list|(
name|docData
argument_list|,
name|name
argument_list|,
name|date
argument_list|,
operator|new
name|InputSource
argument_list|(
name|reader
argument_list|)
argument_list|,
name|trecSrc
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|saxe
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"SAX exception occurred while parsing HTML document."
argument_list|,
name|saxe
argument_list|)
throw|;
block|}
block|}
DECL|method|parse
specifier|public
name|DocData
name|parse
parameter_list|(
name|DocData
name|docData
parameter_list|,
name|String
name|name
parameter_list|,
name|Date
name|date
parameter_list|,
name|InputSource
name|source
parameter_list|,
name|TrecContentSource
name|trecSrc
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
block|{
specifier|final
name|Parser
name|p
init|=
operator|new
name|Parser
argument_list|(
name|source
argument_list|)
decl_stmt|;
comment|// properties
specifier|final
name|Properties
name|props
init|=
name|p
operator|.
name|metaTags
decl_stmt|;
name|String
name|dateStr
init|=
name|props
operator|.
name|getProperty
argument_list|(
literal|"date"
argument_list|)
decl_stmt|;
if|if
condition|(
name|dateStr
operator|!=
literal|null
condition|)
block|{
specifier|final
name|Date
name|newDate
init|=
name|trecSrc
operator|.
name|parseDate
argument_list|(
name|dateStr
argument_list|)
decl_stmt|;
if|if
condition|(
name|newDate
operator|!=
literal|null
condition|)
block|{
name|date
operator|=
name|newDate
expr_stmt|;
block|}
block|}
name|docData
operator|.
name|clear
argument_list|()
expr_stmt|;
name|docData
operator|.
name|setName
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|docData
operator|.
name|setBody
argument_list|(
name|p
operator|.
name|body
argument_list|)
expr_stmt|;
name|docData
operator|.
name|setTitle
argument_list|(
name|p
operator|.
name|title
argument_list|)
expr_stmt|;
name|docData
operator|.
name|setProps
argument_list|(
name|props
argument_list|)
expr_stmt|;
name|docData
operator|.
name|setDate
argument_list|(
name|date
argument_list|)
expr_stmt|;
return|return
name|docData
return|;
block|}
block|}
end_class
end_unit
