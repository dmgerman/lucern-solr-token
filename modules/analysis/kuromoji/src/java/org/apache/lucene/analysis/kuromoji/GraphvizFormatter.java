begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.kuromoji
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|kuromoji
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|List
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
name|kuromoji
operator|.
name|KuromojiTokenizer
operator|.
name|Position
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
name|kuromoji
operator|.
name|KuromojiTokenizer
operator|.
name|Type
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
name|kuromoji
operator|.
name|KuromojiTokenizer
operator|.
name|WrappedPositionArray
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
name|kuromoji
operator|.
name|dict
operator|.
name|ConnectionCosts
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
name|kuromoji
operator|.
name|dict
operator|.
name|Dictionary
import|;
end_import
begin_comment
comment|// TODO: would be nice to show 2nd best path in a diff't
end_comment
begin_comment
comment|// color...
end_comment
begin_comment
comment|/**  * Outputs the dot (graphviz) string for the viterbi lattice.  */
end_comment
begin_class
DECL|class|GraphvizFormatter
specifier|public
class|class
name|GraphvizFormatter
block|{
DECL|field|BOS_LABEL
specifier|private
specifier|final
specifier|static
name|String
name|BOS_LABEL
init|=
literal|"BOS"
decl_stmt|;
DECL|field|EOS_LABEL
specifier|private
specifier|final
specifier|static
name|String
name|EOS_LABEL
init|=
literal|"EOS"
decl_stmt|;
DECL|field|FONT_NAME
specifier|private
specifier|final
specifier|static
name|String
name|FONT_NAME
init|=
literal|"Helvetica"
decl_stmt|;
DECL|field|costs
specifier|private
specifier|final
name|ConnectionCosts
name|costs
decl_stmt|;
DECL|field|bestPathMap
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|bestPathMap
decl_stmt|;
DECL|field|sb
specifier|private
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
DECL|method|GraphvizFormatter
specifier|public
name|GraphvizFormatter
parameter_list|(
name|ConnectionCosts
name|costs
parameter_list|)
block|{
name|this
operator|.
name|costs
operator|=
name|costs
expr_stmt|;
name|this
operator|.
name|bestPathMap
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|formatHeader
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"  init [style=invis]\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"  init -> 0.0 [label=\""
operator|+
name|BOS_LABEL
operator|+
literal|"\"]\n"
argument_list|)
expr_stmt|;
block|}
DECL|method|finish
specifier|public
name|String
name|finish
parameter_list|()
block|{
name|sb
operator|.
name|append
argument_list|(
name|formatTrailer
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|// Backtraces another incremental fragment:
DECL|method|onBacktrace
name|void
name|onBacktrace
parameter_list|(
name|KuromojiTokenizer
name|tok
parameter_list|,
name|WrappedPositionArray
name|positions
parameter_list|,
name|int
name|lastBackTracePos
parameter_list|,
name|Position
name|endPosData
parameter_list|,
name|int
name|fromIDX
parameter_list|,
name|char
index|[]
name|fragment
parameter_list|,
name|boolean
name|isEnd
parameter_list|)
block|{
name|setBestPathMap
argument_list|(
name|positions
argument_list|,
name|lastBackTracePos
argument_list|,
name|endPosData
argument_list|,
name|fromIDX
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|formatNodes
argument_list|(
name|tok
argument_list|,
name|positions
argument_list|,
name|lastBackTracePos
argument_list|,
name|endPosData
argument_list|,
name|fragment
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|isEnd
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"  fini [style=invis]\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"  "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|getNodeID
argument_list|(
name|endPosData
operator|.
name|pos
argument_list|,
name|fromIDX
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" -> fini [label=\""
operator|+
name|EOS_LABEL
operator|+
literal|"\"]"
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Records which arcs make up the best bath:
DECL|method|setBestPathMap
specifier|private
name|void
name|setBestPathMap
parameter_list|(
name|WrappedPositionArray
name|positions
parameter_list|,
name|int
name|startPos
parameter_list|,
name|Position
name|endPosData
parameter_list|,
name|int
name|fromIDX
parameter_list|)
block|{
name|bestPathMap
operator|.
name|clear
argument_list|()
expr_stmt|;
name|int
name|pos
init|=
name|endPosData
operator|.
name|pos
decl_stmt|;
name|int
name|bestIDX
init|=
name|fromIDX
decl_stmt|;
while|while
condition|(
name|pos
operator|>
name|startPos
condition|)
block|{
specifier|final
name|Position
name|posData
init|=
name|positions
operator|.
name|get
argument_list|(
name|pos
argument_list|)
decl_stmt|;
specifier|final
name|int
name|backPos
init|=
name|posData
operator|.
name|backPos
index|[
name|bestIDX
index|]
decl_stmt|;
specifier|final
name|int
name|backIDX
init|=
name|posData
operator|.
name|backIndex
index|[
name|bestIDX
index|]
decl_stmt|;
specifier|final
name|String
name|toNodeID
init|=
name|getNodeID
argument_list|(
name|pos
argument_list|,
name|bestIDX
argument_list|)
decl_stmt|;
specifier|final
name|String
name|fromNodeID
init|=
name|getNodeID
argument_list|(
name|backPos
argument_list|,
name|backIDX
argument_list|)
decl_stmt|;
assert|assert
operator|!
name|bestPathMap
operator|.
name|containsKey
argument_list|(
name|fromNodeID
argument_list|)
assert|;
assert|assert
operator|!
name|bestPathMap
operator|.
name|containsValue
argument_list|(
name|toNodeID
argument_list|)
assert|;
name|bestPathMap
operator|.
name|put
argument_list|(
name|fromNodeID
argument_list|,
name|toNodeID
argument_list|)
expr_stmt|;
name|pos
operator|=
name|backPos
expr_stmt|;
name|bestIDX
operator|=
name|backIDX
expr_stmt|;
block|}
block|}
DECL|method|formatNodes
specifier|private
name|String
name|formatNodes
parameter_list|(
name|KuromojiTokenizer
name|tok
parameter_list|,
name|WrappedPositionArray
name|positions
parameter_list|,
name|int
name|startPos
parameter_list|,
name|Position
name|endPosData
parameter_list|,
name|char
index|[]
name|fragment
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
comment|// Output nodes
for|for
control|(
name|int
name|pos
init|=
name|startPos
operator|+
literal|1
init|;
name|pos
operator|<=
name|endPosData
operator|.
name|pos
condition|;
name|pos
operator|++
control|)
block|{
specifier|final
name|Position
name|posData
init|=
name|positions
operator|.
name|get
argument_list|(
name|pos
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|posData
operator|.
name|count
condition|;
name|idx
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"  "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|getNodeID
argument_list|(
name|pos
argument_list|,
name|idx
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" [label=\""
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|pos
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|": "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|posData
operator|.
name|lastRightID
index|[
name|idx
index|]
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"\"]\n"
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Output arcs
for|for
control|(
name|int
name|pos
init|=
name|endPosData
operator|.
name|pos
init|;
name|pos
operator|>
name|startPos
condition|;
name|pos
operator|--
control|)
block|{
specifier|final
name|Position
name|posData
init|=
name|positions
operator|.
name|get
argument_list|(
name|pos
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|posData
operator|.
name|count
condition|;
name|idx
operator|++
control|)
block|{
specifier|final
name|Position
name|backPosData
init|=
name|positions
operator|.
name|get
argument_list|(
name|posData
operator|.
name|backPos
index|[
name|idx
index|]
argument_list|)
decl_stmt|;
specifier|final
name|String
name|toNodeID
init|=
name|getNodeID
argument_list|(
name|pos
argument_list|,
name|idx
argument_list|)
decl_stmt|;
specifier|final
name|String
name|fromNodeID
init|=
name|getNodeID
argument_list|(
name|posData
operator|.
name|backPos
index|[
name|idx
index|]
argument_list|,
name|posData
operator|.
name|backIndex
index|[
name|idx
index|]
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"  "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|fromNodeID
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" -> "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|toNodeID
argument_list|)
expr_stmt|;
specifier|final
name|String
name|attrs
decl_stmt|;
if|if
condition|(
name|toNodeID
operator|.
name|equals
argument_list|(
name|bestPathMap
operator|.
name|get
argument_list|(
name|fromNodeID
argument_list|)
argument_list|)
condition|)
block|{
comment|// This arc is on best path
name|attrs
operator|=
literal|" color=\"#40e050\" fontcolor=\"#40a050\" penwidth=3 fontsize=20"
expr_stmt|;
block|}
else|else
block|{
name|attrs
operator|=
literal|""
expr_stmt|;
block|}
specifier|final
name|Dictionary
name|dict
init|=
name|tok
operator|.
name|getDict
argument_list|(
name|posData
operator|.
name|backType
index|[
name|idx
index|]
argument_list|)
decl_stmt|;
specifier|final
name|int
name|wordCost
init|=
name|dict
operator|.
name|getWordCost
argument_list|(
name|posData
operator|.
name|backID
index|[
name|idx
index|]
argument_list|)
decl_stmt|;
specifier|final
name|int
name|bgCost
init|=
name|costs
operator|.
name|get
argument_list|(
name|backPosData
operator|.
name|lastRightID
index|[
name|posData
operator|.
name|backIndex
index|[
name|idx
index|]
index|]
argument_list|,
name|dict
operator|.
name|getLeftId
argument_list|(
name|posData
operator|.
name|backID
index|[
name|idx
index|]
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|String
name|surfaceForm
init|=
operator|new
name|String
argument_list|(
name|fragment
argument_list|,
name|posData
operator|.
name|backPos
index|[
name|idx
index|]
operator|-
name|startPos
argument_list|,
name|pos
operator|-
name|posData
operator|.
name|backPos
index|[
name|idx
index|]
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" [label=\""
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|surfaceForm
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|wordCost
argument_list|)
expr_stmt|;
if|if
condition|(
name|bgCost
operator|>=
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|'+'
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|bgCost
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"\""
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|attrs
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"]\n"
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|formatHeader
specifier|private
name|String
name|formatHeader
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"digraph viterbi {\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"  graph [ fontsize=30 labelloc=\"t\" label=\"\" splines=true overlap=false rankdir = \"LR\"];\n"
argument_list|)
expr_stmt|;
comment|//sb.append("  // A2 paper size\n");
comment|//sb.append("  size = \"34.4,16.5\";\n");
comment|//sb.append("  // try to fill paper\n");
comment|//sb.append("  ratio = fill;\n");
name|sb
operator|.
name|append
argument_list|(
literal|"  edge [ fontname=\""
operator|+
name|FONT_NAME
operator|+
literal|"\" fontcolor=\"red\" color=\"#606060\" ]\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"  node [ style=\"filled\" fillcolor=\"#e8e8f0\" shape=\"Mrecord\" fontname=\""
operator|+
name|FONT_NAME
operator|+
literal|"\" ]\n"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|formatTrailer
specifier|private
name|String
name|formatTrailer
parameter_list|()
block|{
return|return
literal|"}"
return|;
block|}
DECL|method|getNodeID
specifier|private
name|String
name|getNodeID
parameter_list|(
name|int
name|pos
parameter_list|,
name|int
name|idx
parameter_list|)
block|{
return|return
name|pos
operator|+
literal|"."
operator|+
name|idx
return|;
block|}
block|}
end_class
end_unit
