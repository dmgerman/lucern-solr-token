begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.internal.csv.writer
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|internal
operator|.
name|csv
operator|.
name|writer
package|;
end_package
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
name|Collection
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
comment|/**  * The CSVConfig is used to configure the CSV writer  *  */
end_comment
begin_class
DECL|class|CSVConfig
specifier|public
class|class
name|CSVConfig
block|{
comment|/** specifies if it is a fixed width csv file **/
DECL|field|fixedWidth
specifier|private
name|boolean
name|fixedWidth
decl_stmt|;
comment|/** list of fields **/
DECL|field|fields
specifier|private
name|List
name|fields
decl_stmt|;
comment|/** Do no do any filling **/
DECL|field|FILLNONE
specifier|public
specifier|static
specifier|final
name|int
name|FILLNONE
init|=
literal|0
decl_stmt|;
comment|/** Fill content the the left. Mainly usable together with fixedWidth **/
DECL|field|FILLLEFT
specifier|public
specifier|static
specifier|final
name|int
name|FILLLEFT
init|=
literal|1
decl_stmt|;
comment|/** Fill content to the right. Mainly usable together with fixedWidth **/
DECL|field|FILLRIGHT
specifier|public
specifier|static
specifier|final
name|int
name|FILLRIGHT
init|=
literal|2
decl_stmt|;
comment|/** The fill pattern */
DECL|field|fill
specifier|private
name|int
name|fill
decl_stmt|;
comment|/** The fill char. Defaults to a space */
DECL|field|fillChar
specifier|private
name|char
name|fillChar
init|=
literal|' '
decl_stmt|;
comment|/** The seperator character. Defaults to , */
DECL|field|delimiter
specifier|private
name|char
name|delimiter
init|=
literal|','
decl_stmt|;
comment|/** Should we ignore the delimiter. Defaults to false */
DECL|field|ignoreDelimiter
specifier|private
name|boolean
name|ignoreDelimiter
init|=
literal|false
decl_stmt|;
comment|/** the value delimiter. Defaults to " */
DECL|field|valueDelimiter
specifier|private
name|char
name|valueDelimiter
init|=
literal|'"'
decl_stmt|;
comment|/** Should we ignore the value delimiter. Defaults to true */
DECL|field|ignoreValueDelimiter
specifier|private
name|boolean
name|ignoreValueDelimiter
init|=
literal|true
decl_stmt|;
comment|/** Specifies if we want to use a field header */
DECL|field|fieldHeader
specifier|private
name|boolean
name|fieldHeader
init|=
literal|false
decl_stmt|;
comment|/** Specifies if the end of the line needs to be trimmed */
DECL|field|endTrimmed
specifier|private
name|boolean
name|endTrimmed
init|=
literal|false
decl_stmt|;
comment|/**      *       */
DECL|method|CSVConfig
specifier|public
name|CSVConfig
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/**      * @return if the CSV file is fixedWidth      */
DECL|method|isFixedWidth
specifier|public
name|boolean
name|isFixedWidth
parameter_list|()
block|{
return|return
name|fixedWidth
return|;
block|}
comment|/**      * Specify if the CSV file is fixed width.      * Defaults to false      * @param fixedWidth the fixedwidth      */
DECL|method|setFixedWidth
specifier|public
name|void
name|setFixedWidth
parameter_list|(
name|boolean
name|fixedWidth
parameter_list|)
block|{
name|this
operator|.
name|fixedWidth
operator|=
name|fixedWidth
expr_stmt|;
block|}
DECL|method|addField
specifier|public
name|void
name|addField
parameter_list|(
name|CSVField
name|field
parameter_list|)
block|{
if|if
condition|(
name|fields
operator|==
literal|null
condition|)
block|{
name|fields
operator|=
operator|new
name|ArrayList
argument_list|()
expr_stmt|;
block|}
name|fields
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
comment|/**      * Set the fields that should be used by the writer.      * This will overwrite currently added fields completely!      * @param csvFields the csvfields array. If null it will do nothing      */
DECL|method|setFields
specifier|public
name|void
name|setFields
parameter_list|(
name|CSVField
index|[]
name|csvFields
parameter_list|)
block|{
if|if
condition|(
name|csvFields
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|fields
operator|=
operator|new
name|ArrayList
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|csvFields
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Set the fields that should be used by the writer      * @param csvField a collection with fields. If null it will do nothing      */
DECL|method|setFields
specifier|public
name|void
name|setFields
parameter_list|(
name|Collection
name|csvField
parameter_list|)
block|{
if|if
condition|(
name|csvField
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|fields
operator|=
operator|new
name|ArrayList
argument_list|(
name|csvField
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return an array with the known fields (even if no fields are specified)      */
DECL|method|getFields
specifier|public
name|CSVField
index|[]
name|getFields
parameter_list|()
block|{
name|CSVField
index|[]
name|csvFields
init|=
operator|new
name|CSVField
index|[
literal|0
index|]
decl_stmt|;
if|if
condition|(
name|fields
operator|!=
literal|null
condition|)
block|{
return|return
operator|(
name|CSVField
index|[]
operator|)
name|fields
operator|.
name|toArray
argument_list|(
name|csvFields
argument_list|)
return|;
block|}
return|return
name|csvFields
return|;
block|}
DECL|method|getField
specifier|public
name|CSVField
name|getField
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|fields
operator|==
literal|null
operator|||
name|name
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fields
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|CSVField
name|field
init|=
operator|(
name|CSVField
operator|)
name|fields
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|field
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**      * @return the fill pattern.      */
DECL|method|getFill
specifier|public
name|int
name|getFill
parameter_list|()
block|{
return|return
name|fill
return|;
block|}
comment|/**      * Set the fill pattern. Defaults to {@link #FILLNONE}      *<br>Other options are : {@link #FILLLEFT} and {@link #FILLRIGHT}      * @param fill the fill pattern.      */
DECL|method|setFill
specifier|public
name|void
name|setFill
parameter_list|(
name|int
name|fill
parameter_list|)
block|{
name|this
operator|.
name|fill
operator|=
name|fill
expr_stmt|;
block|}
comment|/**      *       * @return the fillchar. Defaults to a space.      */
DECL|method|getFillChar
specifier|public
name|char
name|getFillChar
parameter_list|()
block|{
return|return
name|fillChar
return|;
block|}
comment|/**      * Set the fill char      * @param fillChar the fill char      */
DECL|method|setFillChar
specifier|public
name|void
name|setFillChar
parameter_list|(
name|char
name|fillChar
parameter_list|)
block|{
name|this
operator|.
name|fillChar
operator|=
name|fillChar
expr_stmt|;
block|}
comment|/**      * @return the delimeter used.      */
DECL|method|getDelimiter
specifier|public
name|char
name|getDelimiter
parameter_list|()
block|{
return|return
name|delimiter
return|;
block|}
comment|/**      * Set the delimiter to use      * @param delimiter the delimiter character.      */
DECL|method|setDelimiter
specifier|public
name|void
name|setDelimiter
parameter_list|(
name|char
name|delimiter
parameter_list|)
block|{
name|this
operator|.
name|delimiter
operator|=
name|delimiter
expr_stmt|;
block|}
comment|/**      * @return if the writer should ignore the delimiter character.      */
DECL|method|isDelimiterIgnored
specifier|public
name|boolean
name|isDelimiterIgnored
parameter_list|()
block|{
return|return
name|ignoreDelimiter
return|;
block|}
comment|/**      * Specify if the writer should ignore the delimiter.       * @param ignoreDelimiter defaults to false.      */
DECL|method|setIgnoreDelimiter
specifier|public
name|void
name|setIgnoreDelimiter
parameter_list|(
name|boolean
name|ignoreDelimiter
parameter_list|)
block|{
name|this
operator|.
name|ignoreDelimiter
operator|=
name|ignoreDelimiter
expr_stmt|;
block|}
comment|/**      * @return the value delimeter used. Defaults to "      */
DECL|method|getValueDelimiter
specifier|public
name|char
name|getValueDelimiter
parameter_list|()
block|{
return|return
name|valueDelimiter
return|;
block|}
comment|/**      * Set the value delimiter to use      * @param valueDelimiter the value delimiter character.      */
DECL|method|setValueDelimiter
specifier|public
name|void
name|setValueDelimiter
parameter_list|(
name|char
name|valueDelimiter
parameter_list|)
block|{
name|this
operator|.
name|valueDelimiter
operator|=
name|valueDelimiter
expr_stmt|;
block|}
comment|/**      * @return if the writer should ignore the value delimiter character.      *         Defaults to true.      */
DECL|method|isValueDelimiterIgnored
specifier|public
name|boolean
name|isValueDelimiterIgnored
parameter_list|()
block|{
return|return
name|ignoreValueDelimiter
return|;
block|}
comment|/**      * Specify if the writer should ignore the value delimiter.       * @param ignoreValueDelimiter defaults to false.      */
DECL|method|setIgnoreValueDelimiter
specifier|public
name|void
name|setIgnoreValueDelimiter
parameter_list|(
name|boolean
name|ignoreValueDelimiter
parameter_list|)
block|{
name|this
operator|.
name|ignoreValueDelimiter
operator|=
name|ignoreValueDelimiter
expr_stmt|;
block|}
comment|/**      * @return if a field header is used. Defaults to false      */
DECL|method|isFieldHeader
specifier|public
name|boolean
name|isFieldHeader
parameter_list|()
block|{
return|return
name|fieldHeader
return|;
block|}
comment|/**      * Specify if you want to use a field header.      * @param fieldHeader true or false.      */
DECL|method|setFieldHeader
specifier|public
name|void
name|setFieldHeader
parameter_list|(
name|boolean
name|fieldHeader
parameter_list|)
block|{
name|this
operator|.
name|fieldHeader
operator|=
name|fieldHeader
expr_stmt|;
block|}
comment|/**      * TODO..      * @see java.lang.Object#equals(java.lang.Object)      */
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|==
literal|null
operator|&&
operator|!
operator|(
name|obj
operator|instanceof
name|CSVConfig
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|super
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
return|;
comment|//        CSVConfig config = (CSVConfig) obj;
comment|//        getFill() == config.getFill()
comment|//        getFields().equals(config.getFields())
block|}
comment|/**      * Creates a config based on a stream. It tries to guess<br>      * NOTE : The stream will be closed.      * @param inputStream the inputstream.       * @return the guessed config.       */
DECL|method|guessConfig
specifier|public
specifier|static
name|CSVConfig
name|guessConfig
parameter_list|(
name|InputStream
name|inputStream
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
comment|/**      * @return if the end of the line should be trimmed. Default is false.      */
DECL|method|isEndTrimmed
specifier|public
name|boolean
name|isEndTrimmed
parameter_list|()
block|{
return|return
name|endTrimmed
return|;
block|}
comment|/**      * Specify if the end of the line needs to be trimmed. Defaults to false.      */
DECL|method|setEndTrimmed
specifier|public
name|void
name|setEndTrimmed
parameter_list|(
name|boolean
name|endTrimmed
parameter_list|)
block|{
name|this
operator|.
name|endTrimmed
operator|=
name|endTrimmed
expr_stmt|;
block|}
block|}
end_class
end_unit
