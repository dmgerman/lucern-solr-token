begin_unit
begin_comment
comment|/*  * $Id$  *   * Copyright 1997 Hewlett-Packard Company  *   * This file may be copied, modified and distributed only in  * accordance with the terms of the limited licence contained  * in the accompanying file LICENSE.TXT.  */
end_comment
begin_package
DECL|package|hplb.xml
package|package
name|hplb
operator|.
name|xml
package|;
end_package
begin_import
import|import
name|hplb
operator|.
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|*
import|;
end_import
begin_import
import|import
name|hplb
operator|.
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|*
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import
begin_comment
comment|/**  * Parses a stream of MarkupTokens into a tree structure.  * Uses Tokenizer.  *   *<p>This class has very shallow (no) understanding of HTML. Correct  * handling of&lt;p&gt; tags requires some special code as does correct  * handling of&lt;li&gt;. This parser doesn't know that an "li" tag can  * be terminated by another "li" tag or a "ul" end tag. Hence "li" is  * treated as an empty tag here which means that in the generated parse  * tree the children of the "li" element are represented as siblings of it.  *   * @see Tokenizer  * @author  Anders Kristensen  */
end_comment
begin_class
DECL|class|Parser
specifier|public
class|class
name|Parser
implements|implements
name|DocumentHandler
block|{
comment|// FIXME: add support for discriminate per-element whitespace handling
comment|/**      * Set of elements which the parser will expect to be empty, i.e. it      * will not expect an end tag (e.g. IMG, META HTML elements).      * End tags for any of these are ignored...      */
DECL|field|emptyElms
specifier|protected
name|Hashtable
name|emptyElms
init|=
operator|new
name|Hashtable
argument_list|()
decl_stmt|;
comment|/**      * Maps element names to a list of names of other elements which      * terminate that element. So for example "dt" might be mapped to      * ("dt", "dd") and "p" might be mapped to all blocklevel HTML      * elements.      */
DECL|field|terminators
specifier|protected
name|Hashtable
name|terminators
init|=
operator|new
name|Hashtable
argument_list|()
decl_stmt|;
DECL|field|tok
specifier|protected
name|Tokenizer
name|tok
decl_stmt|;
DECL|field|dom
specifier|protected
name|DOM
name|dom
decl_stmt|;
DECL|field|root
specifier|protected
name|Document
name|root
decl_stmt|;
DECL|field|current
specifier|protected
name|Node
name|current
decl_stmt|;
comment|/**      * Non-fatal errors are written to this PrintStream. Fatal errors      * are reported as Exceptions.      */
DECL|field|err
name|PrintStream
name|err
init|=
name|System
operator|.
name|err
decl_stmt|;
DECL|method|Parser
specifier|public
name|Parser
parameter_list|()
block|{
name|tok
operator|=
operator|new
name|Tokenizer
argument_list|()
expr_stmt|;
name|tok
operator|.
name|setDocumentHandler
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|dom
operator|=
operator|new
name|DOMImpl
argument_list|()
expr_stmt|;
block|}
DECL|method|setDOM
specifier|public
name|DOM
name|setDOM
parameter_list|(
name|DOM
name|dom
parameter_list|)
block|{
name|DOM
name|old
init|=
name|dom
decl_stmt|;
name|this
operator|.
name|dom
operator|=
name|dom
expr_stmt|;
return|return
name|old
return|;
block|}
DECL|method|getTokenizer
specifier|public
name|Tokenizer
name|getTokenizer
parameter_list|()
block|{
return|return
name|tok
return|;
block|}
comment|/**      * Add the set of HTML empty elements to the set of tags recognized      * as empty tags.      */
DECL|method|addEmptyElms
specifier|public
name|void
name|addEmptyElms
parameter_list|(
name|String
index|[]
name|elms
parameter_list|)
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
name|elms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|emptyElms
operator|.
name|put
argument_list|(
name|elms
index|[
name|i
index|]
argument_list|,
name|elms
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|clearEmptyElmSet
specifier|public
name|void
name|clearEmptyElmSet
parameter_list|()
block|{
name|emptyElms
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
DECL|method|isEmptyElm
specifier|public
name|boolean
name|isEmptyElm
parameter_list|(
name|String
name|elmName
parameter_list|)
block|{
return|return
name|emptyElms
operator|.
name|get
argument_list|(
name|elmName
argument_list|)
operator|!=
literal|null
return|;
block|}
DECL|method|setElmTerminators
specifier|public
name|void
name|setElmTerminators
parameter_list|(
name|String
name|elmName
parameter_list|,
name|String
index|[]
name|elmTerms
parameter_list|)
block|{
name|terminators
operator|.
name|put
argument_list|(
name|elmName
argument_list|,
name|putIds
argument_list|(
operator|new
name|Hashtable
argument_list|()
argument_list|,
name|elmTerms
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|addTerminator
specifier|public
name|void
name|addTerminator
parameter_list|(
name|String
name|elmName
parameter_list|,
name|String
name|elmTerm
parameter_list|)
block|{
name|Hashtable
name|h
init|=
operator|(
name|Hashtable
operator|)
name|terminators
operator|.
name|get
argument_list|(
name|elmName
argument_list|)
decl_stmt|;
if|if
condition|(
name|h
operator|==
literal|null
condition|)
name|terminators
operator|.
name|put
argument_list|(
name|elmName
argument_list|,
name|h
operator|=
operator|new
name|Hashtable
argument_list|()
argument_list|)
expr_stmt|;
name|h
operator|.
name|put
argument_list|(
name|elmTerm
argument_list|,
name|elmTerm
argument_list|)
expr_stmt|;
block|}
DECL|method|putIds
specifier|public
specifier|static
specifier|final
name|Dictionary
name|putIds
parameter_list|(
name|Dictionary
name|dict
parameter_list|,
name|String
index|[]
name|sary
parameter_list|)
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
name|sary
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|dict
operator|.
name|put
argument_list|(
name|sary
index|[
name|i
index|]
argument_list|,
name|sary
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|dict
return|;
block|}
DECL|method|root
specifier|protected
name|Document
name|root
parameter_list|()
block|{
return|return
name|root
return|;
block|}
DECL|method|parse
specifier|public
name|Document
name|parse
parameter_list|(
name|InputStream
name|in
parameter_list|)
throws|throws
name|Exception
block|{
name|root
operator|=
name|dom
operator|.
name|createDocument
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|current
operator|=
name|root
expr_stmt|;
name|tok
operator|.
name|parse
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|root
argument_list|()
return|;
block|}
DECL|method|startDocument
specifier|public
name|void
name|startDocument
parameter_list|()
block|{}
DECL|method|endDocument
specifier|public
name|void
name|endDocument
parameter_list|()
block|{}
comment|// FIXME: record in root DOCUMENT the id's of elements which have one
DECL|method|doctype
specifier|public
name|void
name|doctype
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|publicID
parameter_list|,
name|String
name|systemID
parameter_list|)
block|{     }
DECL|method|startElement
specifier|public
name|void
name|startElement
parameter_list|(
name|String
name|name
parameter_list|,
name|AttributeMap
name|attributes
parameter_list|)
block|{
comment|//System.out.println("CURRENT: " + current);
comment|// does this new element terminate the current element?
if|if
condition|(
name|current
operator|!=
name|root
condition|)
block|{
name|String
name|tagName
init|=
operator|(
operator|(
name|Element
operator|)
name|current
operator|)
operator|.
name|getTagName
argument_list|()
decl_stmt|;
if|if
condition|(
name|tagName
operator|!=
literal|null
condition|)
block|{
name|Hashtable
name|terms
init|=
operator|(
name|Hashtable
operator|)
name|terminators
operator|.
name|get
argument_list|(
name|tagName
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|!=
literal|null
operator|&&
name|terms
operator|.
name|get
argument_list|(
name|name
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|current
operator|=
name|current
operator|.
name|getParentNode
argument_list|()
expr_stmt|;
comment|// FIXME: could be null
block|}
block|}
block|}
name|Element
name|elm
init|=
name|root
operator|.
name|createElement
argument_list|(
name|name
argument_list|,
name|getDOMAttrs
argument_list|(
name|attributes
argument_list|)
argument_list|)
decl_stmt|;
comment|// FIXME:<hr> gets written as<hr></hr> - the following line changes
comment|// this tp<hr/> which is even wors - we should distinguish between
comment|// those two types of empty elements.
name|current
operator|.
name|insertBefore
argument_list|(
name|elm
argument_list|,
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|isEmptyElm
argument_list|(
name|name
argument_list|)
condition|)
name|current
operator|=
name|elm
expr_stmt|;
block|}
DECL|method|endElement
specifier|public
name|void
name|endElement
parameter_list|(
name|String
name|name
parameter_list|)
block|{
comment|// we go up the parse tree till we find the node which matches
comment|// this end tag. This mechanism elegantly handles "implicitly
comment|// closed" elements such as<li> being terminated by an
comment|// enclosing<ul> being ended.
comment|//System.out.println("CURRENT: " + current);
name|Node
name|node
init|=
name|current
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
if|if
condition|(
name|node
operator|==
name|root
condition|)
block|{
name|err
operator|.
name|println
argument_list|(
literal|"Stray end tag ignored: "
operator|+
name|name
operator|+
literal|" line "
operator|+
name|tok
operator|.
name|line
operator|+
literal|" column "
operator|+
name|tok
operator|.
name|column
argument_list|)
expr_stmt|;
return|return;
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|Element
operator|)
name|node
operator|)
operator|.
name|getTagName
argument_list|()
argument_list|)
condition|)
block|{
name|current
operator|=
name|node
operator|.
name|getParentNode
argument_list|()
expr_stmt|;
return|return;
block|}
else|else
block|{
name|node
operator|=
name|node
operator|.
name|getParentNode
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|characters
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
block|{
name|current
operator|.
name|insertBefore
argument_list|(
name|root
operator|.
name|createTextNode
argument_list|(
operator|new
name|String
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|ignorable
specifier|public
name|void
name|ignorable
parameter_list|(
name|char
name|ch
index|[]
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Ignorable ws: "
operator|+
operator|new
name|String
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|processingInstruction
specifier|public
name|void
name|processingInstruction
parameter_list|(
name|String
name|target
parameter_list|,
name|String
name|remainder
parameter_list|)
block|{
comment|// FIXME: the DOM says 2nd arg should be everything between "<?" and "?>"
name|current
operator|.
name|insertBefore
argument_list|(
name|root
operator|.
name|createPI
argument_list|(
name|target
argument_list|,
name|remainder
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|getDOMAttrs
specifier|public
name|AttributeList
name|getDOMAttrs
parameter_list|(
name|AttributeMap
name|attrs
parameter_list|)
block|{
name|String
name|name
decl_stmt|;
name|Node
name|value
decl_stmt|;
name|Enumeration
name|e
decl_stmt|;
name|AttributeList
name|domAttrs
init|=
name|root
operator|.
name|createAttributeList
argument_list|()
decl_stmt|;
for|for
control|(
name|e
operator|=
name|attrs
operator|.
name|getAttributeNames
argument_list|()
init|;
name|e
operator|.
name|hasMoreElements
argument_list|()
condition|;
control|)
block|{
name|name
operator|=
operator|(
name|String
operator|)
name|e
operator|.
name|nextElement
argument_list|()
expr_stmt|;
name|value
operator|=
name|root
operator|.
name|createTextNode
argument_list|(
name|attrs
operator|.
name|getValue
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
name|domAttrs
operator|.
name|setAttribute
argument_list|(
name|root
operator|.
name|createAttribute
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|domAttrs
return|;
block|}
comment|// for debugging
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|Parser
name|parser
init|=
operator|new
name|Parser
argument_list|()
decl_stmt|;
name|Document
name|doc
init|=
name|parser
operator|.
name|parse
argument_list|(
name|System
operator|.
name|in
argument_list|)
decl_stmt|;
name|Utils
operator|.
name|pp
argument_list|(
name|doc
argument_list|,
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
