begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.internal.csv
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
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import
begin_comment
comment|/**  * CSVStrategy  *  * Represents the strategy for a CSV.  */
end_comment
begin_class
DECL|class|CSVStrategy
specifier|public
class|class
name|CSVStrategy
implements|implements
name|Cloneable
implements|,
name|Serializable
block|{
DECL|field|delimiter
specifier|private
name|char
name|delimiter
decl_stmt|;
DECL|field|encapsulator
specifier|private
name|char
name|encapsulator
decl_stmt|;
DECL|field|commentStart
specifier|private
name|char
name|commentStart
decl_stmt|;
DECL|field|escape
specifier|private
name|char
name|escape
decl_stmt|;
DECL|field|ignoreLeadingWhitespaces
specifier|private
name|boolean
name|ignoreLeadingWhitespaces
decl_stmt|;
DECL|field|ignoreTrailingWhitespaces
specifier|private
name|boolean
name|ignoreTrailingWhitespaces
decl_stmt|;
DECL|field|interpretUnicodeEscapes
specifier|private
name|boolean
name|interpretUnicodeEscapes
decl_stmt|;
DECL|field|ignoreEmptyLines
specifier|private
name|boolean
name|ignoreEmptyLines
decl_stmt|;
comment|// controls for output
DECL|field|printerNewline
specifier|private
name|String
name|printerNewline
decl_stmt|;
comment|// -2 is used to signal disabled, because it won't be confused with
comment|// an EOF signal (-1), and because \ufffe in UTF-16 would be
comment|// encoded as two chars (using surrogates) and thus there should never
comment|// be a collision with a real text char.
DECL|field|COMMENTS_DISABLED
specifier|public
specifier|static
name|char
name|COMMENTS_DISABLED
init|=
operator|(
name|char
operator|)
operator|-
literal|2
decl_stmt|;
DECL|field|ESCAPE_DISABLED
specifier|public
specifier|static
name|char
name|ESCAPE_DISABLED
init|=
operator|(
name|char
operator|)
operator|-
literal|2
decl_stmt|;
DECL|field|ENCAPSULATOR_DISABLED
specifier|public
specifier|static
name|char
name|ENCAPSULATOR_DISABLED
init|=
operator|(
name|char
operator|)
operator|-
literal|2
decl_stmt|;
DECL|field|DEFAULT_PRINTER_NEWLINE
specifier|public
specifier|static
name|String
name|DEFAULT_PRINTER_NEWLINE
init|=
literal|"\n"
decl_stmt|;
DECL|field|DEFAULT_STRATEGY
specifier|public
specifier|static
specifier|final
name|CSVStrategy
name|DEFAULT_STRATEGY
init|=
operator|new
name|ImmutableCSVStrategy
argument_list|(
literal|','
argument_list|,
literal|'"'
argument_list|,
name|COMMENTS_DISABLED
argument_list|,
name|ESCAPE_DISABLED
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
name|DEFAULT_PRINTER_NEWLINE
argument_list|)
decl_stmt|;
DECL|field|EXCEL_STRATEGY
specifier|public
specifier|static
specifier|final
name|CSVStrategy
name|EXCEL_STRATEGY
init|=
operator|new
name|ImmutableCSVStrategy
argument_list|(
literal|','
argument_list|,
literal|'"'
argument_list|,
name|COMMENTS_DISABLED
argument_list|,
name|ESCAPE_DISABLED
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
name|DEFAULT_PRINTER_NEWLINE
argument_list|)
decl_stmt|;
DECL|field|TDF_STRATEGY
specifier|public
specifier|static
specifier|final
name|CSVStrategy
name|TDF_STRATEGY
init|=
operator|new
name|ImmutableCSVStrategy
argument_list|(
literal|'\t'
argument_list|,
literal|'"'
argument_list|,
name|COMMENTS_DISABLED
argument_list|,
name|ESCAPE_DISABLED
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
name|DEFAULT_PRINTER_NEWLINE
argument_list|)
decl_stmt|;
DECL|method|CSVStrategy
specifier|public
name|CSVStrategy
parameter_list|(
name|char
name|delimiter
parameter_list|,
name|char
name|encapsulator
parameter_list|,
name|char
name|commentStart
parameter_list|)
block|{
name|this
argument_list|(
name|delimiter
argument_list|,
name|encapsulator
argument_list|,
name|commentStart
argument_list|,
name|ESCAPE_DISABLED
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
name|DEFAULT_PRINTER_NEWLINE
argument_list|)
expr_stmt|;
block|}
comment|/**    * Customized CSV strategy setter.    *    * @param delimiter a Char used for value separation    * @param encapsulator a Char used as value encapsulation marker    * @param commentStart a Char used for comment identification    * @param escape a Char used for escaping    * @param ignoreTrailingWhitespaces TRUE when trailing whitespaces should be    *                                 ignored    * @param ignoreLeadingWhitespaces TRUE when leading whitespaces should be    *                                ignored    * @param interpretUnicodeEscapes TRUE when unicode escapes should be    *                                interpreted    * @param ignoreEmptyLines TRUE when the parser should skip emtpy lines    * @param printerNewline The string to use when printing a newline    */
DECL|method|CSVStrategy
specifier|public
name|CSVStrategy
parameter_list|(
name|char
name|delimiter
parameter_list|,
name|char
name|encapsulator
parameter_list|,
name|char
name|commentStart
parameter_list|,
name|char
name|escape
parameter_list|,
name|boolean
name|ignoreLeadingWhitespaces
parameter_list|,
name|boolean
name|ignoreTrailingWhitespaces
parameter_list|,
name|boolean
name|interpretUnicodeEscapes
parameter_list|,
name|boolean
name|ignoreEmptyLines
parameter_list|,
name|String
name|printerNewline
parameter_list|)
block|{
name|this
operator|.
name|delimiter
operator|=
name|delimiter
expr_stmt|;
name|this
operator|.
name|encapsulator
operator|=
name|encapsulator
expr_stmt|;
name|this
operator|.
name|commentStart
operator|=
name|commentStart
expr_stmt|;
name|this
operator|.
name|escape
operator|=
name|escape
expr_stmt|;
name|this
operator|.
name|ignoreLeadingWhitespaces
operator|=
name|ignoreLeadingWhitespaces
expr_stmt|;
name|this
operator|.
name|ignoreTrailingWhitespaces
operator|=
name|ignoreTrailingWhitespaces
expr_stmt|;
name|this
operator|.
name|interpretUnicodeEscapes
operator|=
name|interpretUnicodeEscapes
expr_stmt|;
name|this
operator|.
name|ignoreEmptyLines
operator|=
name|ignoreEmptyLines
expr_stmt|;
name|this
operator|.
name|printerNewline
operator|=
name|printerNewline
expr_stmt|;
block|}
comment|/**    * Customized CSV strategy setter.    *    * @param delimiter a Char used for value separation    * @param encapsulator a Char used as value encapsulation marker    * @param commentStart a Char used for comment identification    * @param escape a Char used for escaping    * @param ignoreTrailingWhitespaces TRUE when trailing whitespaces should be    *                                 ignored    * @param ignoreLeadingWhitespaces TRUE when leading whitespaces should be    *                                ignored    * @param interpretUnicodeEscapes TRUE when unicode escapes should be    *                                interpreted    * @param ignoreEmptyLines TRUE when the parser should skip emtpy lines    * @deprecated Use the ctor that also takes printerNewline.  This ctor will be removed in Solr 7.    */
annotation|@
name|Deprecated
DECL|method|CSVStrategy
specifier|public
name|CSVStrategy
parameter_list|(
name|char
name|delimiter
parameter_list|,
name|char
name|encapsulator
parameter_list|,
name|char
name|commentStart
parameter_list|,
name|char
name|escape
parameter_list|,
name|boolean
name|ignoreLeadingWhitespaces
parameter_list|,
name|boolean
name|ignoreTrailingWhitespaces
parameter_list|,
name|boolean
name|interpretUnicodeEscapes
parameter_list|,
name|boolean
name|ignoreEmptyLines
parameter_list|)
block|{
name|this
argument_list|(
name|delimiter
argument_list|,
name|encapsulator
argument_list|,
name|commentStart
argument_list|,
name|escape
argument_list|,
name|ignoreLeadingWhitespaces
argument_list|,
name|ignoreTrailingWhitespaces
argument_list|,
name|interpretUnicodeEscapes
argument_list|,
name|ignoreEmptyLines
argument_list|,
name|DEFAULT_PRINTER_NEWLINE
argument_list|)
expr_stmt|;
block|}
comment|/** @deprecated will be removed in Solr 7 */
annotation|@
name|Deprecated
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
DECL|method|getDelimiter
specifier|public
name|char
name|getDelimiter
parameter_list|()
block|{
return|return
name|this
operator|.
name|delimiter
return|;
block|}
comment|/** @deprecated will be removed in Solr 7 */
annotation|@
name|Deprecated
DECL|method|setEncapsulator
specifier|public
name|void
name|setEncapsulator
parameter_list|(
name|char
name|encapsulator
parameter_list|)
block|{
name|this
operator|.
name|encapsulator
operator|=
name|encapsulator
expr_stmt|;
block|}
DECL|method|getEncapsulator
specifier|public
name|char
name|getEncapsulator
parameter_list|()
block|{
return|return
name|this
operator|.
name|encapsulator
return|;
block|}
comment|/** @deprecated will be removed in Solr 7 */
annotation|@
name|Deprecated
DECL|method|setCommentStart
specifier|public
name|void
name|setCommentStart
parameter_list|(
name|char
name|commentStart
parameter_list|)
block|{
name|this
operator|.
name|commentStart
operator|=
name|commentStart
expr_stmt|;
block|}
DECL|method|getCommentStart
specifier|public
name|char
name|getCommentStart
parameter_list|()
block|{
return|return
name|this
operator|.
name|commentStart
return|;
block|}
DECL|method|isCommentingDisabled
specifier|public
name|boolean
name|isCommentingDisabled
parameter_list|()
block|{
return|return
name|this
operator|.
name|commentStart
operator|==
name|COMMENTS_DISABLED
return|;
block|}
comment|/** @deprecated will be removed in Solr 7 */
annotation|@
name|Deprecated
DECL|method|setEscape
specifier|public
name|void
name|setEscape
parameter_list|(
name|char
name|escape
parameter_list|)
block|{
name|this
operator|.
name|escape
operator|=
name|escape
expr_stmt|;
block|}
DECL|method|getEscape
specifier|public
name|char
name|getEscape
parameter_list|()
block|{
return|return
name|this
operator|.
name|escape
return|;
block|}
comment|/** @deprecated will be removed in Solr 7 */
annotation|@
name|Deprecated
DECL|method|setIgnoreLeadingWhitespaces
specifier|public
name|void
name|setIgnoreLeadingWhitespaces
parameter_list|(
name|boolean
name|ignoreLeadingWhitespaces
parameter_list|)
block|{
name|this
operator|.
name|ignoreLeadingWhitespaces
operator|=
name|ignoreLeadingWhitespaces
expr_stmt|;
block|}
DECL|method|getIgnoreLeadingWhitespaces
specifier|public
name|boolean
name|getIgnoreLeadingWhitespaces
parameter_list|()
block|{
return|return
name|this
operator|.
name|ignoreLeadingWhitespaces
return|;
block|}
comment|/** @deprecated will be removed in Solr 7 */
annotation|@
name|Deprecated
DECL|method|setIgnoreTrailingWhitespaces
specifier|public
name|void
name|setIgnoreTrailingWhitespaces
parameter_list|(
name|boolean
name|ignoreTrailingWhitespaces
parameter_list|)
block|{
name|this
operator|.
name|ignoreTrailingWhitespaces
operator|=
name|ignoreTrailingWhitespaces
expr_stmt|;
block|}
DECL|method|getIgnoreTrailingWhitespaces
specifier|public
name|boolean
name|getIgnoreTrailingWhitespaces
parameter_list|()
block|{
return|return
name|this
operator|.
name|ignoreTrailingWhitespaces
return|;
block|}
comment|/** @deprecated will be removed in Solr 7 */
annotation|@
name|Deprecated
DECL|method|setUnicodeEscapeInterpretation
specifier|public
name|void
name|setUnicodeEscapeInterpretation
parameter_list|(
name|boolean
name|interpretUnicodeEscapes
parameter_list|)
block|{
name|this
operator|.
name|interpretUnicodeEscapes
operator|=
name|interpretUnicodeEscapes
expr_stmt|;
block|}
DECL|method|getUnicodeEscapeInterpretation
specifier|public
name|boolean
name|getUnicodeEscapeInterpretation
parameter_list|()
block|{
return|return
name|this
operator|.
name|interpretUnicodeEscapes
return|;
block|}
comment|/** @deprecated will be removed in Solr 7 */
annotation|@
name|Deprecated
DECL|method|setIgnoreEmptyLines
specifier|public
name|void
name|setIgnoreEmptyLines
parameter_list|(
name|boolean
name|ignoreEmptyLines
parameter_list|)
block|{
name|this
operator|.
name|ignoreEmptyLines
operator|=
name|ignoreEmptyLines
expr_stmt|;
block|}
DECL|method|getIgnoreEmptyLines
specifier|public
name|boolean
name|getIgnoreEmptyLines
parameter_list|()
block|{
return|return
name|this
operator|.
name|ignoreEmptyLines
return|;
block|}
comment|/** @deprecated will be removed in Solr 7 */
annotation|@
name|Deprecated
DECL|method|setPrinterNewline
specifier|public
name|void
name|setPrinterNewline
parameter_list|(
name|String
name|newline
parameter_list|)
block|{
name|this
operator|.
name|printerNewline
operator|=
name|newline
expr_stmt|;
block|}
DECL|method|getPrinterNewline
specifier|public
name|String
name|getPrinterNewline
parameter_list|()
block|{
return|return
name|this
operator|.
name|printerNewline
return|;
block|}
comment|/** @deprecated will be removed in Solr 7 */
annotation|@
name|Deprecated
annotation|@
name|Override
DECL|method|clone
specifier|public
name|Object
name|clone
parameter_list|()
block|{
try|try
block|{
return|return
name|super
operator|.
name|clone
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|CloneNotSupportedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
comment|// impossible
block|}
block|}
block|}
end_class
begin_comment
comment|/**  * @deprecated will be removed in Solr 7  * @lucene.internal  */
end_comment
begin_class
annotation|@
name|Deprecated
class|class
DECL|class|ImmutableCSVStrategy
name|ImmutableCSVStrategy
extends|extends
name|CSVStrategy
block|{
DECL|method|ImmutableCSVStrategy
name|ImmutableCSVStrategy
parameter_list|(
name|char
name|delimiter
parameter_list|,
name|char
name|encapsulator
parameter_list|,
name|char
name|commentStart
parameter_list|,
name|char
name|escape
parameter_list|,
name|boolean
name|ignoreLeadingWhitespaces
parameter_list|,
name|boolean
name|ignoreTrailingWhitespaces
parameter_list|,
name|boolean
name|interpretUnicodeEscapes
parameter_list|,
name|boolean
name|ignoreEmptyLines
parameter_list|,
name|String
name|printerNewline
parameter_list|)
block|{
name|super
argument_list|(
name|delimiter
argument_list|,
name|encapsulator
argument_list|,
name|commentStart
argument_list|,
name|escape
argument_list|,
name|ignoreLeadingWhitespaces
argument_list|,
name|ignoreTrailingWhitespaces
argument_list|,
name|interpretUnicodeEscapes
argument_list|,
name|ignoreEmptyLines
argument_list|,
name|printerNewline
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setDelimiter
specifier|public
name|void
name|setDelimiter
parameter_list|(
name|char
name|delimiter
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|setEncapsulator
specifier|public
name|void
name|setEncapsulator
parameter_list|(
name|char
name|encapsulator
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|setCommentStart
specifier|public
name|void
name|setCommentStart
parameter_list|(
name|char
name|commentStart
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|setEscape
specifier|public
name|void
name|setEscape
parameter_list|(
name|char
name|escape
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|setIgnoreLeadingWhitespaces
specifier|public
name|void
name|setIgnoreLeadingWhitespaces
parameter_list|(
name|boolean
name|ignoreLeadingWhitespaces
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|setIgnoreTrailingWhitespaces
specifier|public
name|void
name|setIgnoreTrailingWhitespaces
parameter_list|(
name|boolean
name|ignoreTrailingWhitespaces
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|setUnicodeEscapeInterpretation
specifier|public
name|void
name|setUnicodeEscapeInterpretation
parameter_list|(
name|boolean
name|interpretUnicodeEscapes
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|setIgnoreEmptyLines
specifier|public
name|void
name|setIgnoreEmptyLines
parameter_list|(
name|boolean
name|ignoreEmptyLines
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|setPrinterNewline
specifier|public
name|void
name|setPrinterNewline
parameter_list|(
name|String
name|newline
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|/** Returns a mutable clone */
annotation|@
name|Override
DECL|method|clone
specifier|public
name|Object
name|clone
parameter_list|()
block|{
return|return
operator|new
name|CSVStrategy
argument_list|(
name|getDelimiter
argument_list|()
argument_list|,
name|getEncapsulator
argument_list|()
argument_list|,
name|getCommentStart
argument_list|()
argument_list|,
name|getEscape
argument_list|()
argument_list|,
name|getIgnoreLeadingWhitespaces
argument_list|()
argument_list|,
name|getIgnoreTrailingWhitespaces
argument_list|()
argument_list|,
name|getUnicodeEscapeInterpretation
argument_list|()
argument_list|,
name|getIgnoreEmptyLines
argument_list|()
argument_list|,
name|getPrinterNewline
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class
end_unit
