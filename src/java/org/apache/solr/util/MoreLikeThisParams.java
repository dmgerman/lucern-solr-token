begin_unit
begin_package
DECL|package|org.apache.solr.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
package|;
end_package
begin_interface
DECL|interface|MoreLikeThisParams
specifier|public
interface|interface
name|MoreLikeThisParams
block|{
comment|// enable more like this -- this only applies to 'StandardRequestHandler' maybe DismaxRequestHandler
DECL|field|MLT
specifier|public
specifier|final
specifier|static
name|String
name|MLT
init|=
literal|"mlt"
decl_stmt|;
DECL|field|PREFIX
specifier|public
specifier|final
specifier|static
name|String
name|PREFIX
init|=
literal|"mlt."
decl_stmt|;
DECL|field|SIMILARITY_FIELDS
specifier|public
specifier|final
specifier|static
name|String
name|SIMILARITY_FIELDS
init|=
name|PREFIX
operator|+
literal|"fl"
decl_stmt|;
DECL|field|MIN_TERM_FREQ
specifier|public
specifier|final
specifier|static
name|String
name|MIN_TERM_FREQ
init|=
name|PREFIX
operator|+
literal|"mintf"
decl_stmt|;
DECL|field|MIN_DOC_FREQ
specifier|public
specifier|final
specifier|static
name|String
name|MIN_DOC_FREQ
init|=
name|PREFIX
operator|+
literal|"mindf"
decl_stmt|;
DECL|field|MIN_WORD_LEN
specifier|public
specifier|final
specifier|static
name|String
name|MIN_WORD_LEN
init|=
name|PREFIX
operator|+
literal|"minwl"
decl_stmt|;
DECL|field|MAX_WORD_LEN
specifier|public
specifier|final
specifier|static
name|String
name|MAX_WORD_LEN
init|=
name|PREFIX
operator|+
literal|"maxwl"
decl_stmt|;
DECL|field|MAX_QUERY_TERMS
specifier|public
specifier|final
specifier|static
name|String
name|MAX_QUERY_TERMS
init|=
name|PREFIX
operator|+
literal|"maxqt"
decl_stmt|;
DECL|field|MAX_NUM_TOKENS_PARSED
specifier|public
specifier|final
specifier|static
name|String
name|MAX_NUM_TOKENS_PARSED
init|=
name|PREFIX
operator|+
literal|"maxntp"
decl_stmt|;
DECL|field|BOOST
specifier|public
specifier|final
specifier|static
name|String
name|BOOST
init|=
name|PREFIX
operator|+
literal|"boost"
decl_stmt|;
comment|// boost or not?
comment|// the /mlt request handler uses 'rows'
DECL|field|DOC_COUNT
specifier|public
specifier|final
specifier|static
name|String
name|DOC_COUNT
init|=
name|PREFIX
operator|+
literal|"count"
decl_stmt|;
comment|// Do you want to include the original document in the results or not
DECL|field|MATCH_INCLUDE
specifier|public
specifier|final
specifier|static
name|String
name|MATCH_INCLUDE
init|=
name|PREFIX
operator|+
literal|"match.include"
decl_stmt|;
comment|// If multiple docs are matched in the query, what offset do you want?
DECL|field|MATCH_OFFSET
specifier|public
specifier|final
specifier|static
name|String
name|MATCH_OFFSET
init|=
name|PREFIX
operator|+
literal|"match.offset"
decl_stmt|;
comment|// Do you want to include the original document in the results or not
DECL|field|INTERESTING_TERMS
specifier|public
specifier|final
specifier|static
name|String
name|INTERESTING_TERMS
init|=
name|PREFIX
operator|+
literal|"interestingTerms"
decl_stmt|;
comment|// false,details,(list or true)
DECL|enum|TermStyle
specifier|public
enum|enum
name|TermStyle
block|{
DECL|enum constant|NONE
name|NONE
block|,
DECL|enum constant|LIST
name|LIST
block|,
DECL|enum constant|DETAILS
name|DETAILS
block|;
DECL|method|get
specifier|public
specifier|static
name|TermStyle
name|get
parameter_list|(
name|String
name|p
parameter_list|)
block|{
if|if
condition|(
name|p
operator|!=
literal|null
condition|)
block|{
name|p
operator|=
name|p
operator|.
name|toUpperCase
argument_list|()
expr_stmt|;
if|if
condition|(
name|p
operator|.
name|equals
argument_list|(
literal|"DETAILS"
argument_list|)
condition|)
block|{
return|return
name|DETAILS
return|;
block|}
elseif|else
if|if
condition|(
name|p
operator|.
name|equals
argument_list|(
literal|"LIST"
argument_list|)
condition|)
block|{
return|return
name|LIST
return|;
block|}
block|}
return|return
name|NONE
return|;
block|}
block|}
block|}
end_interface
end_unit
