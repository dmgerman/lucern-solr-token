begin_unit
begin_comment
comment|/* Generated By:JavaCC: Do not edit this line. PrecedenceQueryParserConstants.java */
end_comment
begin_package
DECL|package|org.apache.lucene.queryParser.precedence
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|precedence
package|;
end_package
begin_comment
comment|/**  * Token literal values and constants.  * Generated by org.javacc.parser.OtherFilesGen#start()  */
end_comment
begin_interface
DECL|interface|PrecedenceQueryParserConstants
specifier|public
interface|interface
name|PrecedenceQueryParserConstants
block|{
comment|/** End of File. */
DECL|field|EOF
name|int
name|EOF
init|=
literal|0
decl_stmt|;
comment|/** RegularExpression Id. */
DECL|field|_NUM_CHAR
name|int
name|_NUM_CHAR
init|=
literal|1
decl_stmt|;
comment|/** RegularExpression Id. */
DECL|field|_ESCAPED_CHAR
name|int
name|_ESCAPED_CHAR
init|=
literal|2
decl_stmt|;
comment|/** RegularExpression Id. */
DECL|field|_TERM_START_CHAR
name|int
name|_TERM_START_CHAR
init|=
literal|3
decl_stmt|;
comment|/** RegularExpression Id. */
DECL|field|_TERM_CHAR
name|int
name|_TERM_CHAR
init|=
literal|4
decl_stmt|;
comment|/** RegularExpression Id. */
DECL|field|_WHITESPACE
name|int
name|_WHITESPACE
init|=
literal|5
decl_stmt|;
comment|/** RegularExpression Id. */
DECL|field|AND
name|int
name|AND
init|=
literal|7
decl_stmt|;
comment|/** RegularExpression Id. */
DECL|field|OR
name|int
name|OR
init|=
literal|8
decl_stmt|;
comment|/** RegularExpression Id. */
DECL|field|NOT
name|int
name|NOT
init|=
literal|9
decl_stmt|;
comment|/** RegularExpression Id. */
DECL|field|PLUS
name|int
name|PLUS
init|=
literal|10
decl_stmt|;
comment|/** RegularExpression Id. */
DECL|field|MINUS
name|int
name|MINUS
init|=
literal|11
decl_stmt|;
comment|/** RegularExpression Id. */
DECL|field|LPAREN
name|int
name|LPAREN
init|=
literal|12
decl_stmt|;
comment|/** RegularExpression Id. */
DECL|field|RPAREN
name|int
name|RPAREN
init|=
literal|13
decl_stmt|;
comment|/** RegularExpression Id. */
DECL|field|COLON
name|int
name|COLON
init|=
literal|14
decl_stmt|;
comment|/** RegularExpression Id. */
DECL|field|CARAT
name|int
name|CARAT
init|=
literal|15
decl_stmt|;
comment|/** RegularExpression Id. */
DECL|field|QUOTED
name|int
name|QUOTED
init|=
literal|16
decl_stmt|;
comment|/** RegularExpression Id. */
DECL|field|TERM
name|int
name|TERM
init|=
literal|17
decl_stmt|;
comment|/** RegularExpression Id. */
DECL|field|FUZZY_SLOP
name|int
name|FUZZY_SLOP
init|=
literal|18
decl_stmt|;
comment|/** RegularExpression Id. */
DECL|field|PREFIXTERM
name|int
name|PREFIXTERM
init|=
literal|19
decl_stmt|;
comment|/** RegularExpression Id. */
DECL|field|WILDTERM
name|int
name|WILDTERM
init|=
literal|20
decl_stmt|;
comment|/** RegularExpression Id. */
DECL|field|REGEXPTERM
name|int
name|REGEXPTERM
init|=
literal|21
decl_stmt|;
comment|/** RegularExpression Id. */
DECL|field|RANGEIN_START
name|int
name|RANGEIN_START
init|=
literal|22
decl_stmt|;
comment|/** RegularExpression Id. */
DECL|field|RANGEEX_START
name|int
name|RANGEEX_START
init|=
literal|23
decl_stmt|;
comment|/** RegularExpression Id. */
DECL|field|NUMBER
name|int
name|NUMBER
init|=
literal|24
decl_stmt|;
comment|/** RegularExpression Id. */
DECL|field|RANGEIN_TO
name|int
name|RANGEIN_TO
init|=
literal|25
decl_stmt|;
comment|/** RegularExpression Id. */
DECL|field|RANGEIN_END
name|int
name|RANGEIN_END
init|=
literal|26
decl_stmt|;
comment|/** RegularExpression Id. */
DECL|field|RANGEIN_QUOTED
name|int
name|RANGEIN_QUOTED
init|=
literal|27
decl_stmt|;
comment|/** RegularExpression Id. */
DECL|field|RANGEIN_GOOP
name|int
name|RANGEIN_GOOP
init|=
literal|28
decl_stmt|;
comment|/** RegularExpression Id. */
DECL|field|RANGEEX_TO
name|int
name|RANGEEX_TO
init|=
literal|29
decl_stmt|;
comment|/** RegularExpression Id. */
DECL|field|RANGEEX_END
name|int
name|RANGEEX_END
init|=
literal|30
decl_stmt|;
comment|/** RegularExpression Id. */
DECL|field|RANGEEX_QUOTED
name|int
name|RANGEEX_QUOTED
init|=
literal|31
decl_stmt|;
comment|/** RegularExpression Id. */
DECL|field|RANGEEX_GOOP
name|int
name|RANGEEX_GOOP
init|=
literal|32
decl_stmt|;
comment|/** Lexical state. */
DECL|field|Boost
name|int
name|Boost
init|=
literal|0
decl_stmt|;
comment|/** Lexical state. */
DECL|field|RangeEx
name|int
name|RangeEx
init|=
literal|1
decl_stmt|;
comment|/** Lexical state. */
DECL|field|RangeIn
name|int
name|RangeIn
init|=
literal|2
decl_stmt|;
comment|/** Lexical state. */
DECL|field|DEFAULT
name|int
name|DEFAULT
init|=
literal|3
decl_stmt|;
comment|/** Literal token values. */
DECL|field|tokenImage
name|String
index|[]
name|tokenImage
init|=
block|{
literal|"<EOF>"
block|,
literal|"<_NUM_CHAR>"
block|,
literal|"<_ESCAPED_CHAR>"
block|,
literal|"<_TERM_START_CHAR>"
block|,
literal|"<_TERM_CHAR>"
block|,
literal|"<_WHITESPACE>"
block|,
literal|"<token of kind 6>"
block|,
literal|"<AND>"
block|,
literal|"<OR>"
block|,
literal|"<NOT>"
block|,
literal|"\"+\""
block|,
literal|"\"-\""
block|,
literal|"\"(\""
block|,
literal|"\")\""
block|,
literal|"\":\""
block|,
literal|"\"^\""
block|,
literal|"<QUOTED>"
block|,
literal|"<TERM>"
block|,
literal|"<FUZZY_SLOP>"
block|,
literal|"<PREFIXTERM>"
block|,
literal|"<WILDTERM>"
block|,
literal|"<REGEXPTERM>"
block|,
literal|"\"[\""
block|,
literal|"\"{\""
block|,
literal|"<NUMBER>"
block|,
literal|"\"TO\""
block|,
literal|"\"]\""
block|,
literal|"<RANGEIN_QUOTED>"
block|,
literal|"<RANGEIN_GOOP>"
block|,
literal|"\"TO\""
block|,
literal|"\"}\""
block|,
literal|"<RANGEEX_QUOTED>"
block|,
literal|"<RANGEEX_GOOP>"
block|,   }
decl_stmt|;
block|}
end_interface
end_unit
