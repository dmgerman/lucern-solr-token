begin_unit
begin_comment
comment|/*  * $Id$  *   * Copyright 1997 Hewlett-Packard Company  *   * This file may be copied, modified and distributed only in  * accordance with the terms of the limited licence contained  * in the accompanying file LICENSE.TXT.  */
end_comment
begin_package
DECL|package|hplb.xml.util
package|package
name|hplb
operator|.
name|xml
operator|.
name|util
package|;
end_package
begin_import
import|import
name|hplb
operator|.
name|xml
operator|.
name|Tokenizer
import|;
end_import
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
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import
begin_class
DECL|class|RmMarkup
specifier|public
class|class
name|RmMarkup
extends|extends
name|HandlerBase
block|{
DECL|field|tok
specifier|static
name|Tokenizer
name|tok
decl_stmt|;
DECL|field|out
specifier|static
name|Writer
name|out
init|=
operator|new
name|OutputStreamWriter
argument_list|(
name|System
operator|.
name|out
argument_list|)
decl_stmt|;
DECL|method|characters
specifier|public
name|void
name|characters
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
throws|throws
name|IOException
block|{
name|out
operator|.
name|write
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
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
operator|new
name|RmMarkup
argument_list|()
argument_list|)
expr_stmt|;
name|TokTest
operator|.
name|args
argument_list|(
name|args
argument_list|,
name|tok
argument_list|)
expr_stmt|;
name|tok
operator|.
name|parse
argument_list|(
name|System
operator|.
name|in
argument_list|)
expr_stmt|;
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
