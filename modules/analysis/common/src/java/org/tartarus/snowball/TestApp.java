begin_unit
begin_comment
comment|/*  Copyright (c) 2001, Dr Martin Porter Copyright (c) 2002, Richard Boulton All rights reserved.  Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:      * Redistributions of source code must retain the above copyright notice,     * this list of conditions and the following disclaimer.     * Redistributions in binary form must reproduce the above copyright     * notice, this list of conditions and the following disclaimer in the     * documentation and/or other materials provided with the distribution.     * Neither the name of the copyright holders nor the names of its contributors     * may be used to endorse or promote products derived from this software     * without specific prior written permission.  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.   */
end_comment
begin_package
DECL|package|org.tartarus.snowball
package|package
name|org
operator|.
name|tartarus
operator|.
name|snowball
package|;
end_package
begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
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
name|Writer
import|;
end_import
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
name|BufferedWriter
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
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
name|OutputStreamWriter
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
import|;
end_import
begin_class
DECL|class|TestApp
specifier|public
class|class
name|TestApp
block|{
DECL|method|usage
specifier|private
specifier|static
name|void
name|usage
parameter_list|()
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Usage: TestApp<algorithm><input file> [-o<output file>]"
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
name|Throwable
block|{
if|if
condition|(
name|args
operator|.
name|length
operator|<
literal|2
condition|)
block|{
name|usage
argument_list|()
expr_stmt|;
return|return;
block|}
name|Class
argument_list|<
name|?
extends|extends
name|SnowballProgram
argument_list|>
name|stemClass
init|=
name|Class
operator|.
name|forName
argument_list|(
literal|"org.tartarus.snowball.ext."
operator|+
name|args
index|[
literal|0
index|]
operator|+
literal|"Stemmer"
argument_list|)
operator|.
name|asSubclass
argument_list|(
name|SnowballProgram
operator|.
name|class
argument_list|)
decl_stmt|;
name|SnowballProgram
name|stemmer
init|=
name|stemClass
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|Method
name|stemMethod
init|=
name|stemClass
operator|.
name|getMethod
argument_list|(
literal|"stem"
argument_list|,
operator|new
name|Class
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|Reader
name|reader
decl_stmt|;
name|reader
operator|=
operator|new
name|InputStreamReader
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|args
index|[
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|reader
operator|=
operator|new
name|BufferedReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|StringBuffer
name|input
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|OutputStream
name|outstream
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|2
condition|)
block|{
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|4
operator|&&
name|args
index|[
literal|2
index|]
operator|.
name|equals
argument_list|(
literal|"-o"
argument_list|)
condition|)
block|{
name|outstream
operator|=
operator|new
name|FileOutputStream
argument_list|(
name|args
index|[
literal|3
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|usage
argument_list|()
expr_stmt|;
return|return;
block|}
block|}
else|else
block|{
name|outstream
operator|=
name|System
operator|.
name|out
expr_stmt|;
block|}
name|Writer
name|output
init|=
operator|new
name|OutputStreamWriter
argument_list|(
name|outstream
argument_list|)
decl_stmt|;
name|output
operator|=
operator|new
name|BufferedWriter
argument_list|(
name|output
argument_list|)
expr_stmt|;
name|int
name|repeat
init|=
literal|1
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|4
condition|)
block|{
name|repeat
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
literal|4
index|]
argument_list|)
expr_stmt|;
block|}
name|Object
index|[]
name|emptyArgs
init|=
operator|new
name|Object
index|[
literal|0
index|]
decl_stmt|;
name|int
name|character
decl_stmt|;
while|while
condition|(
operator|(
name|character
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
name|ch
init|=
operator|(
name|char
operator|)
name|character
decl_stmt|;
if|if
condition|(
name|Character
operator|.
name|isWhitespace
argument_list|(
name|ch
argument_list|)
condition|)
block|{
if|if
condition|(
name|input
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|stemmer
operator|.
name|setCurrent
argument_list|(
name|input
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|repeat
init|;
name|i
operator|!=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|stemMethod
operator|.
name|invoke
argument_list|(
name|stemmer
argument_list|,
name|emptyArgs
argument_list|)
expr_stmt|;
block|}
name|output
operator|.
name|write
argument_list|(
name|stemmer
operator|.
name|getCurrent
argument_list|()
argument_list|)
expr_stmt|;
name|output
operator|.
name|write
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
name|input
operator|.
name|delete
argument_list|(
literal|0
argument_list|,
name|input
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|input
operator|.
name|append
argument_list|(
name|Character
operator|.
name|toLowerCase
argument_list|(
name|ch
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|output
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
