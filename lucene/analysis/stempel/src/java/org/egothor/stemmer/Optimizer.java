begin_unit
begin_comment
comment|/*                     Egothor Software License version 1.00                     Copyright (C) 1997-2004 Leo Galambos.                  Copyright (C) 2002-2004 "Egothor developers"                       on behalf of the Egothor Project.                              All rights reserved.     This  software  is  copyrighted  by  the "Egothor developers". If this    license applies to a single file or document, the "Egothor developers"    are the people or entities mentioned as copyright holders in that file    or  document.  If  this  license  applies  to the Egothor project as a    whole,  the  copyright holders are the people or entities mentioned in    the  file CREDITS. This file can be found in the same location as this    license in the distribution.     Redistribution  and  use  in  source and binary forms, with or without    modification, are permitted provided that the following conditions are    met:     1. Redistributions  of  source  code  must retain the above copyright        notice, the list of contributors, this list of conditions, and the        following disclaimer.     2. Redistributions  in binary form must reproduce the above copyright        notice, the list of contributors, this list of conditions, and the        disclaimer  that  follows  these  conditions  in the documentation        and/or other materials provided with the distribution.     3. The name "Egothor" must not be used to endorse or promote products        derived  from  this software without prior written permission. For        written permission, please contact Leo.G@seznam.cz     4. Products  derived  from this software may not be called "Egothor",        nor  may  "Egothor"  appear  in  their name, without prior written        permission from Leo.G@seznam.cz.     In addition, we request that you include in the end-user documentation    provided  with  the  redistribution  and/or  in the software itself an    acknowledgement equivalent to the following:    "This product includes software developed by the Egothor Project.     http://egothor.sf.net/"     THIS  SOFTWARE  IS  PROVIDED  ``AS  IS''  AND ANY EXPRESSED OR IMPLIED    WARRANTIES,  INCLUDING,  BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF    MERCHANTABILITY  AND  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.    IN  NO  EVENT  SHALL THE EGOTHOR PROJECT OR ITS CONTRIBUTORS BE LIABLE    FOR   ANY   DIRECT,   INDIRECT,  INCIDENTAL,  SPECIAL,  EXEMPLARY,  OR    CONSEQUENTIAL  DAMAGES  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF    SUBSTITUTE  GOODS  OR  SERVICES;  LOSS  OF  USE,  DATA, OR PROFITS; OR    BUSINESS  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,    WHETHER  IN  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE    OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN    IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.     This  software  consists  of  voluntary  contributions  made  by  many    individuals  on  behalf  of  the  Egothor  Project  and was originally    created by Leo Galambos (Leo.G@seznam.cz).  */
end_comment
begin_package
DECL|package|org.egothor.stemmer
package|package
name|org
operator|.
name|egothor
operator|.
name|stemmer
package|;
end_package
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|Iterator
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import
begin_comment
comment|/**  * The Optimizer class is a Trie that will be reduced (have empty rows removed).  *<p>  * The reduction will be made by joining two rows where the first is a subset of  * the second.  */
end_comment
begin_class
DECL|class|Optimizer
specifier|public
class|class
name|Optimizer
extends|extends
name|Reduce
block|{
comment|/**    * Constructor for the Optimizer object.    */
DECL|method|Optimizer
specifier|public
name|Optimizer
parameter_list|()
block|{}
comment|/**    * Optimize (remove empty rows) from the given Trie and return the resulting    * Trie.    *     * @param orig the Trie to consolidate    * @return the newly consolidated Trie    */
annotation|@
name|Override
DECL|method|optimize
specifier|public
name|Trie
name|optimize
parameter_list|(
name|Trie
name|orig
parameter_list|)
block|{
name|List
argument_list|<
name|CharSequence
argument_list|>
name|cmds
init|=
name|orig
operator|.
name|cmds
decl_stmt|;
name|List
argument_list|<
name|Row
argument_list|>
name|rows
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Row
argument_list|>
name|orows
init|=
name|orig
operator|.
name|rows
decl_stmt|;
name|int
name|remap
index|[]
init|=
operator|new
name|int
index|[
name|orows
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
name|orows
operator|.
name|size
argument_list|()
operator|-
literal|1
init|;
name|j
operator|>=
literal|0
condition|;
name|j
operator|--
control|)
block|{
name|Row
name|now
init|=
operator|new
name|Remap
argument_list|(
name|orows
operator|.
name|get
argument_list|(
name|j
argument_list|)
argument_list|,
name|remap
argument_list|)
decl_stmt|;
name|boolean
name|merged
init|=
literal|false
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
name|rows
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Row
name|q
init|=
name|merge
argument_list|(
name|now
argument_list|,
name|rows
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|q
operator|!=
literal|null
condition|)
block|{
name|rows
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|q
argument_list|)
expr_stmt|;
name|merged
operator|=
literal|true
expr_stmt|;
name|remap
index|[
name|j
index|]
operator|=
name|i
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|merged
operator|==
literal|false
condition|)
block|{
name|remap
index|[
name|j
index|]
operator|=
name|rows
operator|.
name|size
argument_list|()
expr_stmt|;
name|rows
operator|.
name|add
argument_list|(
name|now
argument_list|)
expr_stmt|;
block|}
block|}
name|int
name|root
init|=
name|remap
index|[
name|orig
operator|.
name|root
index|]
decl_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|remap
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|rows
operator|=
name|removeGaps
argument_list|(
name|root
argument_list|,
name|rows
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|Row
argument_list|>
argument_list|()
argument_list|,
name|remap
argument_list|)
expr_stmt|;
return|return
operator|new
name|Trie
argument_list|(
name|orig
operator|.
name|forward
argument_list|,
name|remap
index|[
name|root
index|]
argument_list|,
name|cmds
argument_list|,
name|rows
argument_list|)
return|;
block|}
comment|/**    * Merge the given rows and return the resulting Row.    *     * @param master the master Row    * @param existing the existing Row    * @return the resulting Row, or<tt>null</tt> if the operation cannot be    *         realized    */
DECL|method|merge
specifier|public
name|Row
name|merge
parameter_list|(
name|Row
name|master
parameter_list|,
name|Row
name|existing
parameter_list|)
block|{
name|Iterator
argument_list|<
name|Character
argument_list|>
name|i
init|=
name|master
operator|.
name|cells
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|Row
name|n
init|=
operator|new
name|Row
argument_list|()
decl_stmt|;
for|for
control|(
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Character
name|ch
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
comment|// XXX also must handle Cnt and Skip !!
name|Cell
name|a
init|=
name|master
operator|.
name|cells
operator|.
name|get
argument_list|(
name|ch
argument_list|)
decl_stmt|;
name|Cell
name|b
init|=
name|existing
operator|.
name|cells
operator|.
name|get
argument_list|(
name|ch
argument_list|)
decl_stmt|;
name|Cell
name|s
init|=
operator|(
name|b
operator|==
literal|null
operator|)
condition|?
operator|new
name|Cell
argument_list|(
name|a
argument_list|)
else|:
name|merge
argument_list|(
name|a
argument_list|,
name|b
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|n
operator|.
name|cells
operator|.
name|put
argument_list|(
name|ch
argument_list|,
name|s
argument_list|)
expr_stmt|;
block|}
name|i
operator|=
name|existing
operator|.
name|cells
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
expr_stmt|;
for|for
control|(
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Character
name|ch
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|master
operator|.
name|at
argument_list|(
name|ch
argument_list|)
operator|!=
literal|null
condition|)
block|{
continue|continue;
block|}
name|n
operator|.
name|cells
operator|.
name|put
argument_list|(
name|ch
argument_list|,
name|existing
operator|.
name|at
argument_list|(
name|ch
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|n
return|;
block|}
comment|/**    * Merge the given Cells and return the resulting Cell.    *     * @param m the master Cell    * @param e the existing Cell    * @return the resulting Cell, or<tt>null</tt> if the operation cannot be    *         realized    */
DECL|method|merge
specifier|public
name|Cell
name|merge
parameter_list|(
name|Cell
name|m
parameter_list|,
name|Cell
name|e
parameter_list|)
block|{
name|Cell
name|n
init|=
operator|new
name|Cell
argument_list|()
decl_stmt|;
if|if
condition|(
name|m
operator|.
name|skip
operator|!=
name|e
operator|.
name|skip
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|m
operator|.
name|cmd
operator|>=
literal|0
condition|)
block|{
if|if
condition|(
name|e
operator|.
name|cmd
operator|>=
literal|0
condition|)
block|{
if|if
condition|(
name|m
operator|.
name|cmd
operator|==
name|e
operator|.
name|cmd
condition|)
block|{
name|n
operator|.
name|cmd
operator|=
name|m
operator|.
name|cmd
expr_stmt|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
else|else
block|{
name|n
operator|.
name|cmd
operator|=
name|m
operator|.
name|cmd
expr_stmt|;
block|}
block|}
else|else
block|{
name|n
operator|.
name|cmd
operator|=
name|e
operator|.
name|cmd
expr_stmt|;
block|}
if|if
condition|(
name|m
operator|.
name|ref
operator|>=
literal|0
condition|)
block|{
if|if
condition|(
name|e
operator|.
name|ref
operator|>=
literal|0
condition|)
block|{
if|if
condition|(
name|m
operator|.
name|ref
operator|==
name|e
operator|.
name|ref
condition|)
block|{
if|if
condition|(
name|m
operator|.
name|skip
operator|==
name|e
operator|.
name|skip
condition|)
block|{
name|n
operator|.
name|ref
operator|=
name|m
operator|.
name|ref
expr_stmt|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
else|else
block|{
name|n
operator|.
name|ref
operator|=
name|m
operator|.
name|ref
expr_stmt|;
block|}
block|}
else|else
block|{
name|n
operator|.
name|ref
operator|=
name|e
operator|.
name|ref
expr_stmt|;
block|}
name|n
operator|.
name|cnt
operator|=
name|m
operator|.
name|cnt
operator|+
name|e
operator|.
name|cnt
expr_stmt|;
name|n
operator|.
name|skip
operator|=
name|m
operator|.
name|skip
expr_stmt|;
return|return
name|n
return|;
block|}
block|}
end_class
end_unit
