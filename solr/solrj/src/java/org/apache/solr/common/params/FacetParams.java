begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.common.params
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
package|;
end_package
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
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
begin_comment
comment|/**  * Facet parameters  */
end_comment
begin_interface
DECL|interface|FacetParams
specifier|public
interface|interface
name|FacetParams
block|{
comment|/**    * Should facet counts be calculated?    */
DECL|field|FACET
specifier|public
specifier|static
specifier|final
name|String
name|FACET
init|=
literal|"facet"
decl_stmt|;
comment|/**    * Numeric option indicating the maximum number of threads to be used    * in counting facet field vales     */
DECL|field|FACET_THREADS
specifier|public
specifier|static
specifier|final
name|String
name|FACET_THREADS
init|=
name|FACET
operator|+
literal|".threads"
decl_stmt|;
comment|/** What method should be used to do the faceting */
DECL|field|FACET_METHOD
specifier|public
specifier|static
specifier|final
name|String
name|FACET_METHOD
init|=
name|FACET
operator|+
literal|".method"
decl_stmt|;
comment|/** Value for FACET_METHOD param to indicate that Solr should enumerate over terms    * in a field to calculate the facet counts.    */
DECL|field|FACET_METHOD_enum
specifier|public
specifier|static
specifier|final
name|String
name|FACET_METHOD_enum
init|=
literal|"enum"
decl_stmt|;
comment|/** Value for FACET_METHOD param to indicate that Solr should enumerate over documents    * and count up terms by consulting an uninverted representation of the field values    * (such as the FieldCache used for sorting).    */
DECL|field|FACET_METHOD_fc
specifier|public
specifier|static
specifier|final
name|String
name|FACET_METHOD_fc
init|=
literal|"fc"
decl_stmt|;
comment|/** Value for FACET_METHOD param, like FACET_METHOD_fc but counts per-segment.    */
DECL|field|FACET_METHOD_fcs
specifier|public
specifier|static
specifier|final
name|String
name|FACET_METHOD_fcs
init|=
literal|"fcs"
decl_stmt|;
comment|/**    * Any lucene formated queries the user would like to use for    * Facet Constraint Counts (multi-value)    */
DECL|field|FACET_QUERY
specifier|public
specifier|static
specifier|final
name|String
name|FACET_QUERY
init|=
name|FACET
operator|+
literal|".query"
decl_stmt|;
comment|/**    * Any field whose terms the user wants to enumerate over for    * Facet Constraint Counts (multi-value)    */
DECL|field|FACET_FIELD
specifier|public
specifier|static
specifier|final
name|String
name|FACET_FIELD
init|=
name|FACET
operator|+
literal|".field"
decl_stmt|;
comment|/**    * The offset into the list of facets.    * Can be overridden on a per field basis.    */
DECL|field|FACET_OFFSET
specifier|public
specifier|static
specifier|final
name|String
name|FACET_OFFSET
init|=
name|FACET
operator|+
literal|".offset"
decl_stmt|;
comment|/**    * Numeric option indicating the maximum number of facet field counts    * be included in the response for each field - in descending order of count.    * Can be overridden on a per field basis.    */
DECL|field|FACET_LIMIT
specifier|public
specifier|static
specifier|final
name|String
name|FACET_LIMIT
init|=
name|FACET
operator|+
literal|".limit"
decl_stmt|;
comment|/**    * Numeric option indicating the minimum number of hits before a facet should    * be included in the response.  Can be overridden on a per field basis.    */
DECL|field|FACET_MINCOUNT
specifier|public
specifier|static
specifier|final
name|String
name|FACET_MINCOUNT
init|=
name|FACET
operator|+
literal|".mincount"
decl_stmt|;
comment|/**    * Boolean option indicating whether facet field counts of "0" should     * be included in the response.  Can be overridden on a per field basis.    */
DECL|field|FACET_ZEROS
specifier|public
specifier|static
specifier|final
name|String
name|FACET_ZEROS
init|=
name|FACET
operator|+
literal|".zeros"
decl_stmt|;
comment|/**    * Boolean option indicating whether the response should include a     * facet field count for all records which have no value for the     * facet field. Can be overridden on a per field basis.    */
DECL|field|FACET_MISSING
specifier|public
specifier|static
specifier|final
name|String
name|FACET_MISSING
init|=
name|FACET
operator|+
literal|".missing"
decl_stmt|;
DECL|field|FACET_OVERREQUEST
specifier|static
specifier|final
name|String
name|FACET_OVERREQUEST
init|=
name|FACET
operator|+
literal|".overrequest"
decl_stmt|;
comment|/**    * The percentage to over-request by when performing initial distributed requests.    *     * default value is 1.5    */
DECL|field|FACET_OVERREQUEST_RATIO
specifier|public
specifier|static
specifier|final
name|String
name|FACET_OVERREQUEST_RATIO
init|=
name|FACET_OVERREQUEST
operator|+
literal|".ratio"
decl_stmt|;
comment|/**    * An additional amount to over-request by when performing initial distributed requests.  This    * value will be added after accounting for the over-request ratio.    *     * default value is 10    */
DECL|field|FACET_OVERREQUEST_COUNT
specifier|public
specifier|static
specifier|final
name|String
name|FACET_OVERREQUEST_COUNT
init|=
name|FACET_OVERREQUEST
operator|+
literal|".count"
decl_stmt|;
comment|/**    * Comma separated list of fields to pivot    *     * example: author,type  (for types by author / types within author)    */
DECL|field|FACET_PIVOT
specifier|public
specifier|static
specifier|final
name|String
name|FACET_PIVOT
init|=
name|FACET
operator|+
literal|".pivot"
decl_stmt|;
comment|/**    * Minimum number of docs that need to match to be included in the sublist    *     * default value is 1    */
DECL|field|FACET_PIVOT_MINCOUNT
specifier|public
specifier|static
specifier|final
name|String
name|FACET_PIVOT_MINCOUNT
init|=
name|FACET_PIVOT
operator|+
literal|".mincount"
decl_stmt|;
comment|/**    * String option: "count" causes facets to be sorted    * by the count, "index" results in index order.    */
DECL|field|FACET_SORT
specifier|public
specifier|static
specifier|final
name|String
name|FACET_SORT
init|=
name|FACET
operator|+
literal|".sort"
decl_stmt|;
DECL|field|FACET_SORT_COUNT
specifier|public
specifier|static
specifier|final
name|String
name|FACET_SORT_COUNT
init|=
literal|"count"
decl_stmt|;
DECL|field|FACET_SORT_COUNT_LEGACY
specifier|public
specifier|static
specifier|final
name|String
name|FACET_SORT_COUNT_LEGACY
init|=
literal|"true"
decl_stmt|;
DECL|field|FACET_SORT_INDEX
specifier|public
specifier|static
specifier|final
name|String
name|FACET_SORT_INDEX
init|=
literal|"index"
decl_stmt|;
DECL|field|FACET_SORT_INDEX_LEGACY
specifier|public
specifier|static
specifier|final
name|String
name|FACET_SORT_INDEX_LEGACY
init|=
literal|"false"
decl_stmt|;
comment|/**    * Only return constraints of a facet field with the given prefix.    */
DECL|field|FACET_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|FACET_PREFIX
init|=
name|FACET
operator|+
literal|".prefix"
decl_stmt|;
comment|/**    * When faceting by enumerating the terms in a field,    * only use the filterCache for terms with a df>= to this parameter.    */
DECL|field|FACET_ENUM_CACHE_MINDF
specifier|public
specifier|static
specifier|final
name|String
name|FACET_ENUM_CACHE_MINDF
init|=
name|FACET
operator|+
literal|".enum.cache.minDf"
decl_stmt|;
comment|/**    * Any field whose terms the user wants to enumerate over for    * Facet Contraint Counts (multi-value)    */
DECL|field|FACET_DATE
specifier|public
specifier|static
specifier|final
name|String
name|FACET_DATE
init|=
name|FACET
operator|+
literal|".date"
decl_stmt|;
comment|/**    * Date string indicating the starting point for a date facet range.    * Can be overriden on a per field basis.    */
DECL|field|FACET_DATE_START
specifier|public
specifier|static
specifier|final
name|String
name|FACET_DATE_START
init|=
name|FACET_DATE
operator|+
literal|".start"
decl_stmt|;
comment|/**    * Date string indicating the endinging point for a date facet range.    * Can be overriden on a per field basis.    */
DECL|field|FACET_DATE_END
specifier|public
specifier|static
specifier|final
name|String
name|FACET_DATE_END
init|=
name|FACET_DATE
operator|+
literal|".end"
decl_stmt|;
comment|/**    * Date Math string indicating the interval of sub-ranges for a date    * facet range.    * Can be overriden on a per field basis.    */
DECL|field|FACET_DATE_GAP
specifier|public
specifier|static
specifier|final
name|String
name|FACET_DATE_GAP
init|=
name|FACET_DATE
operator|+
literal|".gap"
decl_stmt|;
comment|/**    * Boolean indicating how counts should be computed if the range    * between 'start' and 'end' is not evenly divisible by 'gap'.  If    * this value is true, then all counts of ranges involving the 'end'    * point will use the exact endpoint specified -- this includes the    * 'between' and 'after' counts as well as the last range computed    * using the 'gap'.  If the value is false, then 'gap' is used to    * compute the effective endpoint closest to the 'end' param which    * results in the range between 'start' and 'end' being evenly    * divisible by 'gap'.    * The default is false.    * Can be overriden on a per field basis.    */
DECL|field|FACET_DATE_HARD_END
specifier|public
specifier|static
specifier|final
name|String
name|FACET_DATE_HARD_END
init|=
name|FACET_DATE
operator|+
literal|".hardend"
decl_stmt|;
comment|/**    * String indicating what "other" ranges should be computed for a    * date facet range (multi-value).    * Can be overriden on a per field basis.    * @see FacetRangeOther    */
DECL|field|FACET_DATE_OTHER
specifier|public
specifier|static
specifier|final
name|String
name|FACET_DATE_OTHER
init|=
name|FACET_DATE
operator|+
literal|".other"
decl_stmt|;
comment|/**    *<p>    * Multivalued string indicating what rules should be applied to determine     * when the the ranges generated for date faceting should be inclusive or     * exclusive of their end points.    *</p>    *<p>    * The default value if none are specified is: [lower,upper,edge]<i>(NOTE: This is different then FACET_RANGE_INCLUDE)</i>    *</p>    *<p>    * Can be overriden on a per field basis.    *</p>    * @see FacetRangeInclude    * @see #FACET_RANGE_INCLUDE    */
DECL|field|FACET_DATE_INCLUDE
specifier|public
specifier|static
specifier|final
name|String
name|FACET_DATE_INCLUDE
init|=
name|FACET_DATE
operator|+
literal|".include"
decl_stmt|;
comment|/**    * Any numerical field whose terms the user wants to enumerate over    * Facet Contraint Counts for selected ranges.    */
DECL|field|FACET_RANGE
specifier|public
specifier|static
specifier|final
name|String
name|FACET_RANGE
init|=
name|FACET
operator|+
literal|".range"
decl_stmt|;
comment|/**    * Number indicating the starting point for a numerical range facet.    * Can be overriden on a per field basis.    */
DECL|field|FACET_RANGE_START
specifier|public
specifier|static
specifier|final
name|String
name|FACET_RANGE_START
init|=
name|FACET_RANGE
operator|+
literal|".start"
decl_stmt|;
comment|/**    * Number indicating the ending point for a numerical range facet.    * Can be overriden on a per field basis.    */
DECL|field|FACET_RANGE_END
specifier|public
specifier|static
specifier|final
name|String
name|FACET_RANGE_END
init|=
name|FACET_RANGE
operator|+
literal|".end"
decl_stmt|;
comment|/**    * Number indicating the interval of sub-ranges for a numerical    * facet range.    * Can be overriden on a per field basis.    */
DECL|field|FACET_RANGE_GAP
specifier|public
specifier|static
specifier|final
name|String
name|FACET_RANGE_GAP
init|=
name|FACET_RANGE
operator|+
literal|".gap"
decl_stmt|;
comment|/**    * Boolean indicating how counts should be computed if the range    * between 'start' and 'end' is not evenly divisible by 'gap'.  If    * this value is true, then all counts of ranges involving the 'end'    * point will use the exact endpoint specified -- this includes the    * 'between' and 'after' counts as well as the last range computed    * using the 'gap'.  If the value is false, then 'gap' is used to    * compute the effective endpoint closest to the 'end' param which    * results in the range between 'start' and 'end' being evenly    * divisible by 'gap'.    * The default is false.    * Can be overriden on a per field basis.    */
DECL|field|FACET_RANGE_HARD_END
specifier|public
specifier|static
specifier|final
name|String
name|FACET_RANGE_HARD_END
init|=
name|FACET_RANGE
operator|+
literal|".hardend"
decl_stmt|;
comment|/**    * String indicating what "other" ranges should be computed for a    * numerical range facet (multi-value).    * Can be overriden on a per field basis.    */
DECL|field|FACET_RANGE_OTHER
specifier|public
specifier|static
specifier|final
name|String
name|FACET_RANGE_OTHER
init|=
name|FACET_RANGE
operator|+
literal|".other"
decl_stmt|;
comment|/**    *<p>    * Multivalued string indicating what rules should be applied to determine     * when the the ranges generated for numeric faceting should be inclusive or     * exclusive of their end points.    *</p>    *<p>    * The default value if none are specified is: lower    *</p>    *<p>    * Can be overriden on a per field basis.    *</p>    * @see FacetRangeInclude    */
DECL|field|FACET_RANGE_INCLUDE
specifier|public
specifier|static
specifier|final
name|String
name|FACET_RANGE_INCLUDE
init|=
name|FACET_RANGE
operator|+
literal|".include"
decl_stmt|;
comment|/**    * Any field whose values the user wants to enumerate as explicit intervals of terms.    */
DECL|field|FACET_INTERVAL
specifier|public
specifier|static
specifier|final
name|String
name|FACET_INTERVAL
init|=
name|FACET
operator|+
literal|".interval"
decl_stmt|;
comment|/**    * Set of terms for a single interval to facet on.    */
DECL|field|FACET_INTERVAL_SET
specifier|public
specifier|static
specifier|final
name|String
name|FACET_INTERVAL_SET
init|=
name|FACET_INTERVAL
operator|+
literal|".set"
decl_stmt|;
comment|/**    * An enumeration of the legal values for {@link #FACET_RANGE_OTHER} and {@link #FACET_DATE_OTHER} ...    *<ul>    *<li>before = the count of matches before the start</li>    *<li>after = the count of matches after the end</li>    *<li>between = the count of all matches between start and end</li>    *<li>all = all of the above (default value)</li>    *<li>none = no additional info requested</li>    *</ul>    * @see #FACET_RANGE_OTHER    * @see #FACET_DATE_OTHER    */
DECL|enum|FacetRangeOther
specifier|public
enum|enum
name|FacetRangeOther
block|{
DECL|enum constant|BEFORE
DECL|enum constant|AFTER
DECL|enum constant|BETWEEN
DECL|enum constant|ALL
DECL|enum constant|NONE
name|BEFORE
block|,
name|AFTER
block|,
name|BETWEEN
block|,
name|ALL
block|,
name|NONE
block|;
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|super
operator|.
name|toString
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
return|;
block|}
DECL|method|get
specifier|public
specifier|static
name|FacetRangeOther
name|get
parameter_list|(
name|String
name|label
parameter_list|)
block|{
try|try
block|{
return|return
name|valueOf
argument_list|(
name|label
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|label
operator|+
literal|" is not a valid type of 'other' range facet information"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**    * An enumeration of the legal values for {@link #FACET_DATE_INCLUDE} and {@link #FACET_RANGE_INCLUDE}    *<p>    *<ul>    *<li>lower = all gap based ranges include their lower bound</li>    *<li>upper = all gap based ranges include their upper bound</li>    *<li>edge = the first and last gap ranges include their edge bounds (ie: lower     *     for the first one, upper for the last one) even if the corresponding     *     upper/lower option is not specified    *</li>    *<li>outer = the BEFORE and AFTER ranges     *     should be inclusive of their bounds, even if the first or last ranges     *     already include those boundaries.    *</li>    *<li>all = shorthand for lower, upper, edge, and outer</li>    *</ul>    * @see #FACET_DATE_INCLUDE    * @see #FACET_RANGE_INCLUDE    */
DECL|enum|FacetRangeInclude
specifier|public
enum|enum
name|FacetRangeInclude
block|{
DECL|enum constant|ALL
DECL|enum constant|LOWER
DECL|enum constant|UPPER
DECL|enum constant|EDGE
DECL|enum constant|OUTER
name|ALL
block|,
name|LOWER
block|,
name|UPPER
block|,
name|EDGE
block|,
name|OUTER
block|;
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|super
operator|.
name|toString
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
return|;
block|}
DECL|method|get
specifier|public
specifier|static
name|FacetRangeInclude
name|get
parameter_list|(
name|String
name|label
parameter_list|)
block|{
try|try
block|{
return|return
name|valueOf
argument_list|(
name|label
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|label
operator|+
literal|" is not a valid type of for range 'include' information"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * Convinience method for parsing the param value according to the       * correct semantics and applying the default of "LOWER"      */
DECL|method|parseParam
specifier|public
specifier|static
name|EnumSet
argument_list|<
name|FacetRangeInclude
argument_list|>
name|parseParam
parameter_list|(
specifier|final
name|String
index|[]
name|param
parameter_list|)
block|{
comment|// short circut for default behavior
if|if
condition|(
literal|null
operator|==
name|param
operator|||
literal|0
operator|==
name|param
operator|.
name|length
condition|)
return|return
name|EnumSet
operator|.
name|of
argument_list|(
name|LOWER
argument_list|)
return|;
comment|// build up set containing whatever is specified
specifier|final
name|EnumSet
argument_list|<
name|FacetRangeInclude
argument_list|>
name|include
init|=
name|EnumSet
operator|.
name|noneOf
argument_list|(
name|FacetRangeInclude
operator|.
name|class
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|String
name|o
range|:
name|param
control|)
block|{
name|include
operator|.
name|add
argument_list|(
name|FacetRangeInclude
operator|.
name|get
argument_list|(
name|o
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// if set contains all, then we're back to short circuting
if|if
condition|(
name|include
operator|.
name|contains
argument_list|(
name|FacetRangeInclude
operator|.
name|ALL
argument_list|)
condition|)
return|return
name|EnumSet
operator|.
name|allOf
argument_list|(
name|FacetRangeInclude
operator|.
name|class
argument_list|)
return|;
comment|// use whatever we've got.
return|return
name|include
return|;
block|}
block|}
block|}
end_interface
end_unit
