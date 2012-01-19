begin_unit
begin_package
DECL|package|org.apache.lucene.util.fst
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|fst
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_comment
comment|// Used to dedup states (lookup already-frozen states)
end_comment
begin_class
DECL|class|NodeHash
specifier|final
class|class
name|NodeHash
parameter_list|<
name|T
parameter_list|>
block|{
DECL|field|table
specifier|private
name|int
index|[]
name|table
decl_stmt|;
DECL|field|count
specifier|private
name|int
name|count
decl_stmt|;
DECL|field|mask
specifier|private
name|int
name|mask
decl_stmt|;
DECL|field|fst
specifier|private
specifier|final
name|FST
argument_list|<
name|T
argument_list|>
name|fst
decl_stmt|;
DECL|field|scratchArc
specifier|private
specifier|final
name|FST
operator|.
name|Arc
argument_list|<
name|T
argument_list|>
name|scratchArc
init|=
operator|new
name|FST
operator|.
name|Arc
argument_list|<
name|T
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|NodeHash
specifier|public
name|NodeHash
parameter_list|(
name|FST
argument_list|<
name|T
argument_list|>
name|fst
parameter_list|)
block|{
name|table
operator|=
operator|new
name|int
index|[
literal|16
index|]
expr_stmt|;
name|mask
operator|=
literal|15
expr_stmt|;
name|this
operator|.
name|fst
operator|=
name|fst
expr_stmt|;
block|}
DECL|method|nodesEqual
specifier|private
name|boolean
name|nodesEqual
parameter_list|(
name|Builder
operator|.
name|UnCompiledNode
argument_list|<
name|T
argument_list|>
name|node
parameter_list|,
name|int
name|address
parameter_list|,
name|FST
operator|.
name|BytesReader
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|fst
operator|.
name|readFirstRealArc
argument_list|(
name|address
argument_list|,
name|scratchArc
argument_list|,
name|in
argument_list|)
expr_stmt|;
if|if
condition|(
name|scratchArc
operator|.
name|bytesPerArc
operator|!=
literal|0
operator|&&
name|node
operator|.
name|numArcs
operator|!=
name|scratchArc
operator|.
name|numArcs
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|int
name|arcUpto
init|=
literal|0
init|;
name|arcUpto
operator|<
name|node
operator|.
name|numArcs
condition|;
name|arcUpto
operator|++
control|)
block|{
specifier|final
name|Builder
operator|.
name|Arc
argument_list|<
name|T
argument_list|>
name|arc
init|=
name|node
operator|.
name|arcs
index|[
name|arcUpto
index|]
decl_stmt|;
if|if
condition|(
name|arc
operator|.
name|label
operator|!=
name|scratchArc
operator|.
name|label
operator|||
operator|!
name|arc
operator|.
name|output
operator|.
name|equals
argument_list|(
name|scratchArc
operator|.
name|output
argument_list|)
operator|||
operator|(
operator|(
name|Builder
operator|.
name|CompiledNode
operator|)
name|arc
operator|.
name|target
operator|)
operator|.
name|address
operator|!=
name|scratchArc
operator|.
name|target
operator|||
operator|!
name|arc
operator|.
name|nextFinalOutput
operator|.
name|equals
argument_list|(
name|scratchArc
operator|.
name|nextFinalOutput
argument_list|)
operator|||
name|arc
operator|.
name|isFinal
operator|!=
name|scratchArc
operator|.
name|isFinal
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|scratchArc
operator|.
name|isLast
argument_list|()
condition|)
block|{
if|if
condition|(
name|arcUpto
operator|==
name|node
operator|.
name|numArcs
operator|-
literal|1
condition|)
block|{
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
name|fst
operator|.
name|readNextRealArc
argument_list|(
name|scratchArc
argument_list|,
name|in
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
comment|// hash code for an unfrozen node.  This must be identical
comment|// to the un-frozen case (below)!!
DECL|method|hash
specifier|private
name|int
name|hash
parameter_list|(
name|Builder
operator|.
name|UnCompiledNode
argument_list|<
name|T
argument_list|>
name|node
parameter_list|)
block|{
specifier|final
name|int
name|PRIME
init|=
literal|31
decl_stmt|;
comment|//System.out.println("hash unfrozen");
name|int
name|h
init|=
literal|0
decl_stmt|;
comment|// TODO: maybe if number of arcs is high we can safely subsample?
for|for
control|(
name|int
name|arcIdx
init|=
literal|0
init|;
name|arcIdx
operator|<
name|node
operator|.
name|numArcs
condition|;
name|arcIdx
operator|++
control|)
block|{
specifier|final
name|Builder
operator|.
name|Arc
argument_list|<
name|T
argument_list|>
name|arc
init|=
name|node
operator|.
name|arcs
index|[
name|arcIdx
index|]
decl_stmt|;
comment|//System.out.println("  label=" + arc.label + " target=" + ((Builder.CompiledNode) arc.target).address + " h=" + h + " output=" + fst.outputs.outputToString(arc.output) + " isFinal?=" + arc.isFinal);
name|h
operator|=
name|PRIME
operator|*
name|h
operator|+
name|arc
operator|.
name|label
expr_stmt|;
name|h
operator|=
name|PRIME
operator|*
name|h
operator|+
operator|(
operator|(
name|Builder
operator|.
name|CompiledNode
operator|)
name|arc
operator|.
name|target
operator|)
operator|.
name|address
expr_stmt|;
name|h
operator|=
name|PRIME
operator|*
name|h
operator|+
name|arc
operator|.
name|output
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|h
operator|=
name|PRIME
operator|*
name|h
operator|+
name|arc
operator|.
name|nextFinalOutput
operator|.
name|hashCode
argument_list|()
expr_stmt|;
if|if
condition|(
name|arc
operator|.
name|isFinal
condition|)
block|{
name|h
operator|+=
literal|17
expr_stmt|;
block|}
block|}
comment|//System.out.println("  ret " + (h&Integer.MAX_VALUE));
return|return
name|h
operator|&
name|Integer
operator|.
name|MAX_VALUE
return|;
block|}
comment|// hash code for a frozen node
DECL|method|hash
specifier|private
name|int
name|hash
parameter_list|(
name|int
name|node
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|PRIME
init|=
literal|31
decl_stmt|;
specifier|final
name|FST
operator|.
name|BytesReader
name|in
init|=
name|fst
operator|.
name|getBytesReader
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|//System.out.println("hash frozen");
name|int
name|h
init|=
literal|0
decl_stmt|;
name|fst
operator|.
name|readFirstRealArc
argument_list|(
name|node
argument_list|,
name|scratchArc
argument_list|,
name|in
argument_list|)
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
comment|//System.out.println("  label=" + scratchArc.label + " target=" + scratchArc.target + " h=" + h + " output=" + fst.outputs.outputToString(scratchArc.output) + " next?=" + scratchArc.flag(4) + " final?=" + scratchArc.isFinal());
name|h
operator|=
name|PRIME
operator|*
name|h
operator|+
name|scratchArc
operator|.
name|label
expr_stmt|;
name|h
operator|=
name|PRIME
operator|*
name|h
operator|+
name|scratchArc
operator|.
name|target
expr_stmt|;
name|h
operator|=
name|PRIME
operator|*
name|h
operator|+
name|scratchArc
operator|.
name|output
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|h
operator|=
name|PRIME
operator|*
name|h
operator|+
name|scratchArc
operator|.
name|nextFinalOutput
operator|.
name|hashCode
argument_list|()
expr_stmt|;
if|if
condition|(
name|scratchArc
operator|.
name|isFinal
argument_list|()
condition|)
block|{
name|h
operator|+=
literal|17
expr_stmt|;
block|}
if|if
condition|(
name|scratchArc
operator|.
name|isLast
argument_list|()
condition|)
block|{
break|break;
block|}
name|fst
operator|.
name|readNextRealArc
argument_list|(
name|scratchArc
argument_list|,
name|in
argument_list|)
expr_stmt|;
block|}
comment|//System.out.println("  ret " + (h&Integer.MAX_VALUE));
return|return
name|h
operator|&
name|Integer
operator|.
name|MAX_VALUE
return|;
block|}
DECL|method|add
specifier|public
name|int
name|add
parameter_list|(
name|Builder
operator|.
name|UnCompiledNode
argument_list|<
name|T
argument_list|>
name|node
parameter_list|)
throws|throws
name|IOException
block|{
comment|// System.out.println("hash: add count=" + count + " vs " + table.length);
specifier|final
name|FST
operator|.
name|BytesReader
name|in
init|=
name|fst
operator|.
name|getBytesReader
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|int
name|h
init|=
name|hash
argument_list|(
name|node
argument_list|)
decl_stmt|;
name|int
name|pos
init|=
name|h
operator|&
name|mask
decl_stmt|;
name|int
name|c
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
specifier|final
name|int
name|v
init|=
name|table
index|[
name|pos
index|]
decl_stmt|;
if|if
condition|(
name|v
operator|==
literal|0
condition|)
block|{
comment|// freeze& add
specifier|final
name|int
name|address
init|=
name|fst
operator|.
name|addNode
argument_list|(
name|node
argument_list|)
decl_stmt|;
comment|//System.out.println("  now freeze addr=" + address);
assert|assert
name|hash
argument_list|(
name|address
argument_list|)
operator|==
name|h
operator|:
literal|"frozenHash="
operator|+
name|hash
argument_list|(
name|address
argument_list|)
operator|+
literal|" vs h="
operator|+
name|h
assert|;
name|count
operator|++
expr_stmt|;
name|table
index|[
name|pos
index|]
operator|=
name|address
expr_stmt|;
if|if
condition|(
name|table
operator|.
name|length
operator|<
literal|2
operator|*
name|count
condition|)
block|{
name|rehash
argument_list|()
expr_stmt|;
block|}
return|return
name|address
return|;
block|}
elseif|else
if|if
condition|(
name|nodesEqual
argument_list|(
name|node
argument_list|,
name|v
argument_list|,
name|in
argument_list|)
condition|)
block|{
comment|// same node is already here
return|return
name|v
return|;
block|}
comment|// quadratic probe
name|pos
operator|=
operator|(
name|pos
operator|+
operator|(
operator|++
name|c
operator|)
operator|)
operator|&
name|mask
expr_stmt|;
block|}
block|}
comment|// called only by rehash
DECL|method|addNew
specifier|private
name|void
name|addNew
parameter_list|(
name|int
name|address
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|pos
init|=
name|hash
argument_list|(
name|address
argument_list|)
operator|&
name|mask
decl_stmt|;
name|int
name|c
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|table
index|[
name|pos
index|]
operator|==
literal|0
condition|)
block|{
name|table
index|[
name|pos
index|]
operator|=
name|address
expr_stmt|;
break|break;
block|}
comment|// quadratic probe
name|pos
operator|=
operator|(
name|pos
operator|+
operator|(
operator|++
name|c
operator|)
operator|)
operator|&
name|mask
expr_stmt|;
block|}
block|}
DECL|method|rehash
specifier|private
name|void
name|rehash
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
index|[]
name|oldTable
init|=
name|table
decl_stmt|;
name|table
operator|=
operator|new
name|int
index|[
literal|2
operator|*
name|table
operator|.
name|length
index|]
expr_stmt|;
name|mask
operator|=
name|table
operator|.
name|length
operator|-
literal|1
expr_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|oldTable
operator|.
name|length
condition|;
name|idx
operator|++
control|)
block|{
specifier|final
name|int
name|address
init|=
name|oldTable
index|[
name|idx
index|]
decl_stmt|;
if|if
condition|(
name|address
operator|!=
literal|0
condition|)
block|{
name|addNew
argument_list|(
name|address
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|count
specifier|public
name|int
name|count
parameter_list|()
block|{
return|return
name|count
return|;
block|}
block|}
end_class
end_unit
