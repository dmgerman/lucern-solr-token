begin_unit
begin_package
DECL|package|org.apache.lucene.search.vectorhighlight
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|vectorhighlight
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
name|List
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
name|document
operator|.
name|Document
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
name|document
operator|.
name|Field
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
name|document
operator|.
name|MapFieldSelector
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
name|index
operator|.
name|IndexReader
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
name|search
operator|.
name|highlight
operator|.
name|DefaultEncoder
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
name|search
operator|.
name|highlight
operator|.
name|Encoder
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
name|search
operator|.
name|vectorhighlight
operator|.
name|FieldFragList
operator|.
name|WeightedFragInfo
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
name|search
operator|.
name|vectorhighlight
operator|.
name|FieldFragList
operator|.
name|WeightedFragInfo
operator|.
name|SubInfo
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
name|search
operator|.
name|vectorhighlight
operator|.
name|FieldPhraseList
operator|.
name|WeightedPhraseInfo
operator|.
name|Toffs
import|;
end_import
begin_class
DECL|class|BaseFragmentsBuilder
specifier|public
specifier|abstract
class|class
name|BaseFragmentsBuilder
implements|implements
name|FragmentsBuilder
block|{
DECL|field|preTags
DECL|field|postTags
specifier|protected
name|String
index|[]
name|preTags
decl_stmt|,
name|postTags
decl_stmt|;
DECL|field|COLORED_PRE_TAGS
specifier|public
specifier|static
specifier|final
name|String
index|[]
name|COLORED_PRE_TAGS
init|=
block|{
literal|"<b style=\"background:yellow\">"
block|,
literal|"<b style=\"background:lawngreen\">"
block|,
literal|"<b style=\"background:aquamarine\">"
block|,
literal|"<b style=\"background:magenta\">"
block|,
literal|"<b style=\"background:palegreen\">"
block|,
literal|"<b style=\"background:coral\">"
block|,
literal|"<b style=\"background:wheat\">"
block|,
literal|"<b style=\"background:khaki\">"
block|,
literal|"<b style=\"background:lime\">"
block|,
literal|"<b style=\"background:deepskyblue\">"
block|,
literal|"<b style=\"background:deeppink\">"
block|,
literal|"<b style=\"background:salmon\">"
block|,
literal|"<b style=\"background:peachpuff\">"
block|,
literal|"<b style=\"background:violet\">"
block|,
literal|"<b style=\"background:mediumpurple\">"
block|,
literal|"<b style=\"background:palegoldenrod\">"
block|,
literal|"<b style=\"background:darkkhaki\">"
block|,
literal|"<b style=\"background:springgreen\">"
block|,
literal|"<b style=\"background:turquoise\">"
block|,
literal|"<b style=\"background:powderblue\">"
block|}
decl_stmt|;
DECL|field|COLORED_POST_TAGS
specifier|public
specifier|static
specifier|final
name|String
index|[]
name|COLORED_POST_TAGS
init|=
block|{
literal|"</b>"
block|}
decl_stmt|;
DECL|field|multiValuedSeparator
specifier|private
name|char
name|multiValuedSeparator
init|=
literal|' '
decl_stmt|;
DECL|method|BaseFragmentsBuilder
specifier|protected
name|BaseFragmentsBuilder
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"<b>"
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"</b>"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|BaseFragmentsBuilder
specifier|protected
name|BaseFragmentsBuilder
parameter_list|(
name|String
index|[]
name|preTags
parameter_list|,
name|String
index|[]
name|postTags
parameter_list|)
block|{
name|this
operator|.
name|preTags
operator|=
name|preTags
expr_stmt|;
name|this
operator|.
name|postTags
operator|=
name|postTags
expr_stmt|;
block|}
DECL|method|checkTagsArgument
specifier|static
name|Object
name|checkTagsArgument
parameter_list|(
name|Object
name|tags
parameter_list|)
block|{
if|if
condition|(
name|tags
operator|instanceof
name|String
condition|)
return|return
name|tags
return|;
elseif|else
if|if
condition|(
name|tags
operator|instanceof
name|String
index|[]
condition|)
return|return
name|tags
return|;
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"type of preTags/postTags must be a String or String[]"
argument_list|)
throw|;
block|}
DECL|method|getWeightedFragInfoList
specifier|public
specifier|abstract
name|List
argument_list|<
name|WeightedFragInfo
argument_list|>
name|getWeightedFragInfoList
parameter_list|(
name|List
argument_list|<
name|WeightedFragInfo
argument_list|>
name|src
parameter_list|)
function_decl|;
DECL|field|NULL_ENCODER
specifier|private
specifier|static
specifier|final
name|Encoder
name|NULL_ENCODER
init|=
operator|new
name|DefaultEncoder
argument_list|()
decl_stmt|;
DECL|method|createFragment
specifier|public
name|String
name|createFragment
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|docId
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|FieldFragList
name|fieldFragList
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|createFragment
argument_list|(
name|reader
argument_list|,
name|docId
argument_list|,
name|fieldName
argument_list|,
name|fieldFragList
argument_list|,
name|preTags
argument_list|,
name|postTags
argument_list|,
name|NULL_ENCODER
argument_list|)
return|;
block|}
DECL|method|createFragments
specifier|public
name|String
index|[]
name|createFragments
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|docId
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|FieldFragList
name|fieldFragList
parameter_list|,
name|int
name|maxNumFragments
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|createFragments
argument_list|(
name|reader
argument_list|,
name|docId
argument_list|,
name|fieldName
argument_list|,
name|fieldFragList
argument_list|,
name|maxNumFragments
argument_list|,
name|preTags
argument_list|,
name|postTags
argument_list|,
name|NULL_ENCODER
argument_list|)
return|;
block|}
DECL|method|createFragment
specifier|public
name|String
name|createFragment
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|docId
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|FieldFragList
name|fieldFragList
parameter_list|,
name|String
index|[]
name|preTags
parameter_list|,
name|String
index|[]
name|postTags
parameter_list|,
name|Encoder
name|encoder
parameter_list|)
throws|throws
name|IOException
block|{
name|String
index|[]
name|fragments
init|=
name|createFragments
argument_list|(
name|reader
argument_list|,
name|docId
argument_list|,
name|fieldName
argument_list|,
name|fieldFragList
argument_list|,
literal|1
argument_list|,
name|preTags
argument_list|,
name|postTags
argument_list|,
name|encoder
argument_list|)
decl_stmt|;
if|if
condition|(
name|fragments
operator|==
literal|null
operator|||
name|fragments
operator|.
name|length
operator|==
literal|0
condition|)
return|return
literal|null
return|;
return|return
name|fragments
index|[
literal|0
index|]
return|;
block|}
DECL|method|createFragments
specifier|public
name|String
index|[]
name|createFragments
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|docId
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|FieldFragList
name|fieldFragList
parameter_list|,
name|int
name|maxNumFragments
parameter_list|,
name|String
index|[]
name|preTags
parameter_list|,
name|String
index|[]
name|postTags
parameter_list|,
name|Encoder
name|encoder
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|maxNumFragments
operator|<
literal|0
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"maxNumFragments("
operator|+
name|maxNumFragments
operator|+
literal|") must be positive number."
argument_list|)
throw|;
name|List
argument_list|<
name|WeightedFragInfo
argument_list|>
name|fragInfos
init|=
name|getWeightedFragInfoList
argument_list|(
name|fieldFragList
operator|.
name|fragInfos
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|fragments
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|maxNumFragments
argument_list|)
decl_stmt|;
name|Field
index|[]
name|values
init|=
name|getFields
argument_list|(
name|reader
argument_list|,
name|docId
argument_list|,
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|values
operator|.
name|length
operator|==
literal|0
condition|)
return|return
literal|null
return|;
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
index|[]
name|nextValueIndex
init|=
block|{
literal|0
block|}
decl_stmt|;
for|for
control|(
name|int
name|n
init|=
literal|0
init|;
name|n
operator|<
name|maxNumFragments
operator|&&
name|n
operator|<
name|fragInfos
operator|.
name|size
argument_list|()
condition|;
name|n
operator|++
control|)
block|{
name|WeightedFragInfo
name|fragInfo
init|=
name|fragInfos
operator|.
name|get
argument_list|(
name|n
argument_list|)
decl_stmt|;
name|fragments
operator|.
name|add
argument_list|(
name|makeFragment
argument_list|(
name|buffer
argument_list|,
name|nextValueIndex
argument_list|,
name|values
argument_list|,
name|fragInfo
argument_list|,
name|preTags
argument_list|,
name|postTags
argument_list|,
name|encoder
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|fragments
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|fragments
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
annotation|@
name|Deprecated
DECL|method|getFieldValues
specifier|protected
name|String
index|[]
name|getFieldValues
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|docId
parameter_list|,
name|String
name|fieldName
parameter_list|)
throws|throws
name|IOException
block|{
name|Document
name|doc
init|=
name|reader
operator|.
name|document
argument_list|(
name|docId
argument_list|,
operator|new
name|MapFieldSelector
argument_list|(
operator|new
name|String
index|[]
block|{
name|fieldName
block|}
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|doc
operator|.
name|getValues
argument_list|(
name|fieldName
argument_list|)
return|;
comment|// according to Document class javadoc, this never returns null
block|}
DECL|method|getFields
specifier|protected
name|Field
index|[]
name|getFields
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|docId
parameter_list|,
name|String
name|fieldName
parameter_list|)
throws|throws
name|IOException
block|{
comment|// according to javadoc, doc.getFields(fieldName) cannot be used with lazy loaded field???
name|Document
name|doc
init|=
name|reader
operator|.
name|document
argument_list|(
name|docId
argument_list|,
operator|new
name|MapFieldSelector
argument_list|(
operator|new
name|String
index|[]
block|{
name|fieldName
block|}
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|doc
operator|.
name|getFields
argument_list|(
name|fieldName
argument_list|)
return|;
comment|// according to Document class javadoc, this never returns null
block|}
annotation|@
name|Deprecated
DECL|method|makeFragment
specifier|protected
name|String
name|makeFragment
parameter_list|(
name|StringBuilder
name|buffer
parameter_list|,
name|int
index|[]
name|index
parameter_list|,
name|String
index|[]
name|values
parameter_list|,
name|WeightedFragInfo
name|fragInfo
parameter_list|)
block|{
specifier|final
name|int
name|s
init|=
name|fragInfo
operator|.
name|startOffset
decl_stmt|;
return|return
name|makeFragment
argument_list|(
name|fragInfo
argument_list|,
name|getFragmentSource
argument_list|(
name|buffer
argument_list|,
name|index
argument_list|,
name|values
argument_list|,
name|s
argument_list|,
name|fragInfo
operator|.
name|endOffset
argument_list|)
argument_list|,
name|s
argument_list|,
name|preTags
argument_list|,
name|postTags
argument_list|,
name|NULL_ENCODER
argument_list|)
return|;
block|}
DECL|method|makeFragment
specifier|protected
name|String
name|makeFragment
parameter_list|(
name|StringBuilder
name|buffer
parameter_list|,
name|int
index|[]
name|index
parameter_list|,
name|Field
index|[]
name|values
parameter_list|,
name|WeightedFragInfo
name|fragInfo
parameter_list|,
name|String
index|[]
name|preTags
parameter_list|,
name|String
index|[]
name|postTags
parameter_list|,
name|Encoder
name|encoder
parameter_list|)
block|{
specifier|final
name|int
name|s
init|=
name|fragInfo
operator|.
name|startOffset
decl_stmt|;
return|return
name|makeFragment
argument_list|(
name|fragInfo
argument_list|,
name|getFragmentSource
argument_list|(
name|buffer
argument_list|,
name|index
argument_list|,
name|values
argument_list|,
name|s
argument_list|,
name|fragInfo
operator|.
name|endOffset
argument_list|)
argument_list|,
name|s
argument_list|,
name|preTags
argument_list|,
name|postTags
argument_list|,
name|encoder
argument_list|)
return|;
block|}
DECL|method|makeFragment
specifier|private
name|String
name|makeFragment
parameter_list|(
name|WeightedFragInfo
name|fragInfo
parameter_list|,
name|String
name|src
parameter_list|,
name|int
name|s
parameter_list|,
name|String
index|[]
name|preTags
parameter_list|,
name|String
index|[]
name|postTags
parameter_list|,
name|Encoder
name|encoder
parameter_list|)
block|{
name|StringBuilder
name|fragment
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|srcIndex
init|=
literal|0
decl_stmt|;
for|for
control|(
name|SubInfo
name|subInfo
range|:
name|fragInfo
operator|.
name|subInfos
control|)
block|{
for|for
control|(
name|Toffs
name|to
range|:
name|subInfo
operator|.
name|termsOffsets
control|)
block|{
name|fragment
operator|.
name|append
argument_list|(
name|encoder
operator|.
name|encodeText
argument_list|(
name|src
operator|.
name|substring
argument_list|(
name|srcIndex
argument_list|,
name|to
operator|.
name|startOffset
operator|-
name|s
argument_list|)
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
name|getPreTag
argument_list|(
name|preTags
argument_list|,
name|subInfo
operator|.
name|seqnum
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
name|encoder
operator|.
name|encodeText
argument_list|(
name|src
operator|.
name|substring
argument_list|(
name|to
operator|.
name|startOffset
operator|-
name|s
argument_list|,
name|to
operator|.
name|endOffset
operator|-
name|s
argument_list|)
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
name|getPostTag
argument_list|(
name|postTags
argument_list|,
name|subInfo
operator|.
name|seqnum
argument_list|)
argument_list|)
expr_stmt|;
name|srcIndex
operator|=
name|to
operator|.
name|endOffset
operator|-
name|s
expr_stmt|;
block|}
block|}
name|fragment
operator|.
name|append
argument_list|(
name|encoder
operator|.
name|encodeText
argument_list|(
name|src
operator|.
name|substring
argument_list|(
name|srcIndex
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|fragment
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Deprecated
DECL|method|getFragmentSource
specifier|protected
name|String
name|getFragmentSource
parameter_list|(
name|StringBuilder
name|buffer
parameter_list|,
name|int
index|[]
name|index
parameter_list|,
name|String
index|[]
name|values
parameter_list|,
name|int
name|startOffset
parameter_list|,
name|int
name|endOffset
parameter_list|)
block|{
while|while
condition|(
name|buffer
operator|.
name|length
argument_list|()
operator|<
name|endOffset
operator|&&
name|index
index|[
literal|0
index|]
operator|<
name|values
operator|.
name|length
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
name|values
index|[
name|index
index|[
literal|0
index|]
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|values
index|[
name|index
index|[
literal|0
index|]
index|]
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|&&
name|index
index|[
literal|0
index|]
operator|+
literal|1
operator|<
name|values
operator|.
name|length
condition|)
name|buffer
operator|.
name|append
argument_list|(
name|multiValuedSeparator
argument_list|)
expr_stmt|;
name|index
index|[
literal|0
index|]
operator|++
expr_stmt|;
block|}
name|int
name|eo
init|=
name|buffer
operator|.
name|length
argument_list|()
operator|<
name|endOffset
condition|?
name|buffer
operator|.
name|length
argument_list|()
else|:
name|endOffset
decl_stmt|;
return|return
name|buffer
operator|.
name|substring
argument_list|(
name|startOffset
argument_list|,
name|eo
argument_list|)
return|;
block|}
DECL|method|getFragmentSource
specifier|protected
name|String
name|getFragmentSource
parameter_list|(
name|StringBuilder
name|buffer
parameter_list|,
name|int
index|[]
name|index
parameter_list|,
name|Field
index|[]
name|values
parameter_list|,
name|int
name|startOffset
parameter_list|,
name|int
name|endOffset
parameter_list|)
block|{
while|while
condition|(
name|buffer
operator|.
name|length
argument_list|()
operator|<
name|endOffset
operator|&&
name|index
index|[
literal|0
index|]
operator|<
name|values
operator|.
name|length
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
name|values
index|[
name|index
index|[
literal|0
index|]
index|]
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|values
index|[
name|index
index|[
literal|0
index|]
index|]
operator|.
name|isTokenized
argument_list|()
operator|&&
name|values
index|[
name|index
index|[
literal|0
index|]
index|]
operator|.
name|stringValue
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|&&
name|index
index|[
literal|0
index|]
operator|+
literal|1
operator|<
name|values
operator|.
name|length
condition|)
name|buffer
operator|.
name|append
argument_list|(
name|multiValuedSeparator
argument_list|)
expr_stmt|;
name|index
index|[
literal|0
index|]
operator|++
expr_stmt|;
block|}
name|int
name|eo
init|=
name|buffer
operator|.
name|length
argument_list|()
operator|<
name|endOffset
condition|?
name|buffer
operator|.
name|length
argument_list|()
else|:
name|endOffset
decl_stmt|;
return|return
name|buffer
operator|.
name|substring
argument_list|(
name|startOffset
argument_list|,
name|eo
argument_list|)
return|;
block|}
DECL|method|setMultiValuedSeparator
specifier|public
name|void
name|setMultiValuedSeparator
parameter_list|(
name|char
name|separator
parameter_list|)
block|{
name|multiValuedSeparator
operator|=
name|separator
expr_stmt|;
block|}
DECL|method|getMultiValuedSeparator
specifier|public
name|char
name|getMultiValuedSeparator
parameter_list|()
block|{
return|return
name|multiValuedSeparator
return|;
block|}
DECL|method|getPreTag
specifier|protected
name|String
name|getPreTag
parameter_list|(
name|int
name|num
parameter_list|)
block|{
return|return
name|getPreTag
argument_list|(
name|preTags
argument_list|,
name|num
argument_list|)
return|;
block|}
DECL|method|getPostTag
specifier|protected
name|String
name|getPostTag
parameter_list|(
name|int
name|num
parameter_list|)
block|{
return|return
name|getPostTag
argument_list|(
name|postTags
argument_list|,
name|num
argument_list|)
return|;
block|}
DECL|method|getPreTag
specifier|protected
name|String
name|getPreTag
parameter_list|(
name|String
index|[]
name|preTags
parameter_list|,
name|int
name|num
parameter_list|)
block|{
name|int
name|n
init|=
name|num
operator|%
name|preTags
operator|.
name|length
decl_stmt|;
return|return
name|preTags
index|[
name|n
index|]
return|;
block|}
DECL|method|getPostTag
specifier|protected
name|String
name|getPostTag
parameter_list|(
name|String
index|[]
name|postTags
parameter_list|,
name|int
name|num
parameter_list|)
block|{
name|int
name|n
init|=
name|num
operator|%
name|postTags
operator|.
name|length
decl_stmt|;
return|return
name|postTags
index|[
name|n
index|]
return|;
block|}
block|}
end_class
end_unit
