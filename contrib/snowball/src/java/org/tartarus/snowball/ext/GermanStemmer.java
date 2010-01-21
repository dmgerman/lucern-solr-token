begin_unit
begin_comment
comment|// This file was generated automatically by the Snowball to Java compiler
end_comment
begin_package
DECL|package|org.tartarus.snowball.ext
package|package
name|org
operator|.
name|tartarus
operator|.
name|snowball
operator|.
name|ext
package|;
end_package
begin_import
import|import
name|org
operator|.
name|tartarus
operator|.
name|snowball
operator|.
name|SnowballProgram
import|;
end_import
begin_import
import|import
name|org
operator|.
name|tartarus
operator|.
name|snowball
operator|.
name|Among
import|;
end_import
begin_comment
comment|/**  * Generated class implementing code defined by a snowball script.  */
end_comment
begin_class
DECL|class|GermanStemmer
specifier|public
class|class
name|GermanStemmer
extends|extends
name|SnowballProgram
block|{
DECL|field|a_0
specifier|private
name|Among
name|a_0
index|[]
init|=
block|{
operator|new
name|Among
argument_list|(
literal|""
argument_list|,
operator|-
literal|1
argument_list|,
literal|6
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"U"
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"Y"
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"\u00E4"
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"\u00F6"
argument_list|,
literal|0
argument_list|,
literal|4
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"\u00FC"
argument_list|,
literal|0
argument_list|,
literal|5
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|}
decl_stmt|;
DECL|field|a_1
specifier|private
name|Among
name|a_1
index|[]
init|=
block|{
operator|new
name|Among
argument_list|(
literal|"e"
argument_list|,
operator|-
literal|1
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"em"
argument_list|,
operator|-
literal|1
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"en"
argument_list|,
operator|-
literal|1
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"ern"
argument_list|,
operator|-
literal|1
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"er"
argument_list|,
operator|-
literal|1
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"s"
argument_list|,
operator|-
literal|1
argument_list|,
literal|2
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"es"
argument_list|,
literal|5
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|}
decl_stmt|;
DECL|field|a_2
specifier|private
name|Among
name|a_2
index|[]
init|=
block|{
operator|new
name|Among
argument_list|(
literal|"en"
argument_list|,
operator|-
literal|1
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"er"
argument_list|,
operator|-
literal|1
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"st"
argument_list|,
operator|-
literal|1
argument_list|,
literal|2
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"est"
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|}
decl_stmt|;
DECL|field|a_3
specifier|private
name|Among
name|a_3
index|[]
init|=
block|{
operator|new
name|Among
argument_list|(
literal|"ig"
argument_list|,
operator|-
literal|1
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"lich"
argument_list|,
operator|-
literal|1
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|}
decl_stmt|;
DECL|field|a_4
specifier|private
name|Among
name|a_4
index|[]
init|=
block|{
operator|new
name|Among
argument_list|(
literal|"end"
argument_list|,
operator|-
literal|1
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"ig"
argument_list|,
operator|-
literal|1
argument_list|,
literal|2
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"ung"
argument_list|,
operator|-
literal|1
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"lich"
argument_list|,
operator|-
literal|1
argument_list|,
literal|3
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"isch"
argument_list|,
operator|-
literal|1
argument_list|,
literal|2
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"ik"
argument_list|,
operator|-
literal|1
argument_list|,
literal|2
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"heit"
argument_list|,
operator|-
literal|1
argument_list|,
literal|3
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"keit"
argument_list|,
operator|-
literal|1
argument_list|,
literal|4
argument_list|,
literal|""
argument_list|,
name|this
argument_list|)
block|}
decl_stmt|;
DECL|field|g_v
specifier|private
specifier|static
specifier|final
name|char
name|g_v
index|[]
init|=
block|{
literal|17
block|,
literal|65
block|,
literal|16
block|,
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|8
block|,
literal|0
block|,
literal|32
block|,
literal|8
block|}
decl_stmt|;
DECL|field|g_s_ending
specifier|private
specifier|static
specifier|final
name|char
name|g_s_ending
index|[]
init|=
block|{
literal|117
block|,
literal|30
block|,
literal|5
block|}
decl_stmt|;
DECL|field|g_st_ending
specifier|private
specifier|static
specifier|final
name|char
name|g_st_ending
index|[]
init|=
block|{
literal|117
block|,
literal|30
block|,
literal|4
block|}
decl_stmt|;
DECL|field|I_x
specifier|private
name|int
name|I_x
decl_stmt|;
DECL|field|I_p2
specifier|private
name|int
name|I_p2
decl_stmt|;
DECL|field|I_p1
specifier|private
name|int
name|I_p1
decl_stmt|;
DECL|method|copy_from
specifier|private
name|void
name|copy_from
parameter_list|(
name|GermanStemmer
name|other
parameter_list|)
block|{
name|I_x
operator|=
name|other
operator|.
name|I_x
expr_stmt|;
name|I_p2
operator|=
name|other
operator|.
name|I_p2
expr_stmt|;
name|I_p1
operator|=
name|other
operator|.
name|I_p1
expr_stmt|;
name|super
operator|.
name|copy_from
argument_list|(
name|other
argument_list|)
expr_stmt|;
block|}
DECL|method|r_prelude
specifier|private
name|boolean
name|r_prelude
parameter_list|()
block|{
name|int
name|v_1
decl_stmt|;
name|int
name|v_2
decl_stmt|;
name|int
name|v_3
decl_stmt|;
name|int
name|v_4
decl_stmt|;
name|int
name|v_5
decl_stmt|;
name|int
name|v_6
decl_stmt|;
comment|// (, line 28
comment|// test, line 30
name|v_1
operator|=
name|cursor
expr_stmt|;
comment|// repeat, line 30
name|replab0
label|:
while|while
condition|(
literal|true
condition|)
block|{
name|v_2
operator|=
name|cursor
expr_stmt|;
name|lab1
label|:
do|do
block|{
comment|// (, line 30
comment|// or, line 33
name|lab2
label|:
do|do
block|{
name|v_3
operator|=
name|cursor
expr_stmt|;
name|lab3
label|:
do|do
block|{
comment|// (, line 31
comment|// [, line 32
name|bra
operator|=
name|cursor
expr_stmt|;
comment|// literal, line 32
if|if
condition|(
operator|!
operator|(
name|eq_s
argument_list|(
literal|1
argument_list|,
literal|"\u00DF"
argument_list|)
operator|)
condition|)
block|{
break|break
name|lab3
break|;
block|}
comment|// ], line 32
name|ket
operator|=
name|cursor
expr_stmt|;
comment|//<-, line 32
name|slice_from
argument_list|(
literal|"ss"
argument_list|)
expr_stmt|;
break|break
name|lab2
break|;
block|}
do|while
condition|(
literal|false
condition|)
do|;
name|cursor
operator|=
name|v_3
expr_stmt|;
comment|// next, line 33
if|if
condition|(
name|cursor
operator|>=
name|limit
condition|)
block|{
break|break
name|lab1
break|;
block|}
name|cursor
operator|++
expr_stmt|;
block|}
do|while
condition|(
literal|false
condition|)
do|;
continue|continue
name|replab0
continue|;
block|}
do|while
condition|(
literal|false
condition|)
do|;
name|cursor
operator|=
name|v_2
expr_stmt|;
break|break
name|replab0
break|;
block|}
name|cursor
operator|=
name|v_1
expr_stmt|;
comment|// repeat, line 36
name|replab4
label|:
while|while
condition|(
literal|true
condition|)
block|{
name|v_4
operator|=
name|cursor
expr_stmt|;
name|lab5
label|:
do|do
block|{
comment|// goto, line 36
name|golab6
label|:
while|while
condition|(
literal|true
condition|)
block|{
name|v_5
operator|=
name|cursor
expr_stmt|;
name|lab7
label|:
do|do
block|{
comment|// (, line 36
if|if
condition|(
operator|!
operator|(
name|in_grouping
argument_list|(
name|g_v
argument_list|,
literal|97
argument_list|,
literal|252
argument_list|)
operator|)
condition|)
block|{
break|break
name|lab7
break|;
block|}
comment|// [, line 37
name|bra
operator|=
name|cursor
expr_stmt|;
comment|// or, line 37
name|lab8
label|:
do|do
block|{
name|v_6
operator|=
name|cursor
expr_stmt|;
name|lab9
label|:
do|do
block|{
comment|// (, line 37
comment|// literal, line 37
if|if
condition|(
operator|!
operator|(
name|eq_s
argument_list|(
literal|1
argument_list|,
literal|"u"
argument_list|)
operator|)
condition|)
block|{
break|break
name|lab9
break|;
block|}
comment|// ], line 37
name|ket
operator|=
name|cursor
expr_stmt|;
if|if
condition|(
operator|!
operator|(
name|in_grouping
argument_list|(
name|g_v
argument_list|,
literal|97
argument_list|,
literal|252
argument_list|)
operator|)
condition|)
block|{
break|break
name|lab9
break|;
block|}
comment|//<-, line 37
name|slice_from
argument_list|(
literal|"U"
argument_list|)
expr_stmt|;
break|break
name|lab8
break|;
block|}
do|while
condition|(
literal|false
condition|)
do|;
name|cursor
operator|=
name|v_6
expr_stmt|;
comment|// (, line 38
comment|// literal, line 38
if|if
condition|(
operator|!
operator|(
name|eq_s
argument_list|(
literal|1
argument_list|,
literal|"y"
argument_list|)
operator|)
condition|)
block|{
break|break
name|lab7
break|;
block|}
comment|// ], line 38
name|ket
operator|=
name|cursor
expr_stmt|;
if|if
condition|(
operator|!
operator|(
name|in_grouping
argument_list|(
name|g_v
argument_list|,
literal|97
argument_list|,
literal|252
argument_list|)
operator|)
condition|)
block|{
break|break
name|lab7
break|;
block|}
comment|//<-, line 38
name|slice_from
argument_list|(
literal|"Y"
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
literal|false
condition|)
do|;
name|cursor
operator|=
name|v_5
expr_stmt|;
break|break
name|golab6
break|;
block|}
do|while
condition|(
literal|false
condition|)
do|;
name|cursor
operator|=
name|v_5
expr_stmt|;
if|if
condition|(
name|cursor
operator|>=
name|limit
condition|)
block|{
break|break
name|lab5
break|;
block|}
name|cursor
operator|++
expr_stmt|;
block|}
continue|continue
name|replab4
continue|;
block|}
do|while
condition|(
literal|false
condition|)
do|;
name|cursor
operator|=
name|v_4
expr_stmt|;
break|break
name|replab4
break|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|r_mark_regions
specifier|private
name|boolean
name|r_mark_regions
parameter_list|()
block|{
name|int
name|v_1
decl_stmt|;
comment|// (, line 42
name|I_p1
operator|=
name|limit
expr_stmt|;
name|I_p2
operator|=
name|limit
expr_stmt|;
comment|// test, line 47
name|v_1
operator|=
name|cursor
expr_stmt|;
comment|// (, line 47
comment|// hop, line 47
block|{
name|int
name|c
init|=
name|cursor
operator|+
literal|3
decl_stmt|;
if|if
condition|(
literal|0
operator|>
name|c
operator|||
name|c
operator|>
name|limit
condition|)
block|{
return|return
literal|false
return|;
block|}
name|cursor
operator|=
name|c
expr_stmt|;
block|}
comment|// setmark x, line 47
name|I_x
operator|=
name|cursor
expr_stmt|;
name|cursor
operator|=
name|v_1
expr_stmt|;
comment|// gopast, line 49
name|golab0
label|:
while|while
condition|(
literal|true
condition|)
block|{
name|lab1
label|:
do|do
block|{
if|if
condition|(
operator|!
operator|(
name|in_grouping
argument_list|(
name|g_v
argument_list|,
literal|97
argument_list|,
literal|252
argument_list|)
operator|)
condition|)
block|{
break|break
name|lab1
break|;
block|}
break|break
name|golab0
break|;
block|}
do|while
condition|(
literal|false
condition|)
do|;
if|if
condition|(
name|cursor
operator|>=
name|limit
condition|)
block|{
return|return
literal|false
return|;
block|}
name|cursor
operator|++
expr_stmt|;
block|}
comment|// gopast, line 49
name|golab2
label|:
while|while
condition|(
literal|true
condition|)
block|{
name|lab3
label|:
do|do
block|{
if|if
condition|(
operator|!
operator|(
name|out_grouping
argument_list|(
name|g_v
argument_list|,
literal|97
argument_list|,
literal|252
argument_list|)
operator|)
condition|)
block|{
break|break
name|lab3
break|;
block|}
break|break
name|golab2
break|;
block|}
do|while
condition|(
literal|false
condition|)
do|;
if|if
condition|(
name|cursor
operator|>=
name|limit
condition|)
block|{
return|return
literal|false
return|;
block|}
name|cursor
operator|++
expr_stmt|;
block|}
comment|// setmark p1, line 49
name|I_p1
operator|=
name|cursor
expr_stmt|;
comment|// try, line 50
name|lab4
label|:
do|do
block|{
comment|// (, line 50
if|if
condition|(
operator|!
operator|(
name|I_p1
operator|<
name|I_x
operator|)
condition|)
block|{
break|break
name|lab4
break|;
block|}
name|I_p1
operator|=
name|I_x
expr_stmt|;
block|}
do|while
condition|(
literal|false
condition|)
do|;
comment|// gopast, line 51
name|golab5
label|:
while|while
condition|(
literal|true
condition|)
block|{
name|lab6
label|:
do|do
block|{
if|if
condition|(
operator|!
operator|(
name|in_grouping
argument_list|(
name|g_v
argument_list|,
literal|97
argument_list|,
literal|252
argument_list|)
operator|)
condition|)
block|{
break|break
name|lab6
break|;
block|}
break|break
name|golab5
break|;
block|}
do|while
condition|(
literal|false
condition|)
do|;
if|if
condition|(
name|cursor
operator|>=
name|limit
condition|)
block|{
return|return
literal|false
return|;
block|}
name|cursor
operator|++
expr_stmt|;
block|}
comment|// gopast, line 51
name|golab7
label|:
while|while
condition|(
literal|true
condition|)
block|{
name|lab8
label|:
do|do
block|{
if|if
condition|(
operator|!
operator|(
name|out_grouping
argument_list|(
name|g_v
argument_list|,
literal|97
argument_list|,
literal|252
argument_list|)
operator|)
condition|)
block|{
break|break
name|lab8
break|;
block|}
break|break
name|golab7
break|;
block|}
do|while
condition|(
literal|false
condition|)
do|;
if|if
condition|(
name|cursor
operator|>=
name|limit
condition|)
block|{
return|return
literal|false
return|;
block|}
name|cursor
operator|++
expr_stmt|;
block|}
comment|// setmark p2, line 51
name|I_p2
operator|=
name|cursor
expr_stmt|;
return|return
literal|true
return|;
block|}
DECL|method|r_postlude
specifier|private
name|boolean
name|r_postlude
parameter_list|()
block|{
name|int
name|among_var
decl_stmt|;
name|int
name|v_1
decl_stmt|;
comment|// repeat, line 55
name|replab0
label|:
while|while
condition|(
literal|true
condition|)
block|{
name|v_1
operator|=
name|cursor
expr_stmt|;
name|lab1
label|:
do|do
block|{
comment|// (, line 55
comment|// [, line 57
name|bra
operator|=
name|cursor
expr_stmt|;
comment|// substring, line 57
name|among_var
operator|=
name|find_among
argument_list|(
name|a_0
argument_list|,
literal|6
argument_list|)
expr_stmt|;
if|if
condition|(
name|among_var
operator|==
literal|0
condition|)
block|{
break|break
name|lab1
break|;
block|}
comment|// ], line 57
name|ket
operator|=
name|cursor
expr_stmt|;
switch|switch
condition|(
name|among_var
condition|)
block|{
case|case
literal|0
case|:
break|break
name|lab1
break|;
case|case
literal|1
case|:
comment|// (, line 58
comment|//<-, line 58
name|slice_from
argument_list|(
literal|"y"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|2
case|:
comment|// (, line 59
comment|//<-, line 59
name|slice_from
argument_list|(
literal|"u"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|3
case|:
comment|// (, line 60
comment|//<-, line 60
name|slice_from
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|4
case|:
comment|// (, line 61
comment|//<-, line 61
name|slice_from
argument_list|(
literal|"o"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|5
case|:
comment|// (, line 62
comment|//<-, line 62
name|slice_from
argument_list|(
literal|"u"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|6
case|:
comment|// (, line 63
comment|// next, line 63
if|if
condition|(
name|cursor
operator|>=
name|limit
condition|)
block|{
break|break
name|lab1
break|;
block|}
name|cursor
operator|++
expr_stmt|;
break|break;
block|}
continue|continue
name|replab0
continue|;
block|}
do|while
condition|(
literal|false
condition|)
do|;
name|cursor
operator|=
name|v_1
expr_stmt|;
break|break
name|replab0
break|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|r_R1
specifier|private
name|boolean
name|r_R1
parameter_list|()
block|{
if|if
condition|(
operator|!
operator|(
name|I_p1
operator|<=
name|cursor
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|r_R2
specifier|private
name|boolean
name|r_R2
parameter_list|()
block|{
if|if
condition|(
operator|!
operator|(
name|I_p2
operator|<=
name|cursor
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|r_standard_suffix
specifier|private
name|boolean
name|r_standard_suffix
parameter_list|()
block|{
name|int
name|among_var
decl_stmt|;
name|int
name|v_1
decl_stmt|;
name|int
name|v_2
decl_stmt|;
name|int
name|v_3
decl_stmt|;
name|int
name|v_4
decl_stmt|;
name|int
name|v_5
decl_stmt|;
name|int
name|v_6
decl_stmt|;
name|int
name|v_7
decl_stmt|;
name|int
name|v_8
decl_stmt|;
name|int
name|v_9
decl_stmt|;
comment|// (, line 73
comment|// do, line 74
name|v_1
operator|=
name|limit
operator|-
name|cursor
expr_stmt|;
name|lab0
label|:
do|do
block|{
comment|// (, line 74
comment|// [, line 75
name|ket
operator|=
name|cursor
expr_stmt|;
comment|// substring, line 75
name|among_var
operator|=
name|find_among_b
argument_list|(
name|a_1
argument_list|,
literal|7
argument_list|)
expr_stmt|;
if|if
condition|(
name|among_var
operator|==
literal|0
condition|)
block|{
break|break
name|lab0
break|;
block|}
comment|// ], line 75
name|bra
operator|=
name|cursor
expr_stmt|;
comment|// call R1, line 75
if|if
condition|(
operator|!
name|r_R1
argument_list|()
condition|)
block|{
break|break
name|lab0
break|;
block|}
switch|switch
condition|(
name|among_var
condition|)
block|{
case|case
literal|0
case|:
break|break
name|lab0
break|;
case|case
literal|1
case|:
comment|// (, line 77
comment|// delete, line 77
name|slice_del
argument_list|()
expr_stmt|;
break|break;
case|case
literal|2
case|:
comment|// (, line 80
if|if
condition|(
operator|!
operator|(
name|in_grouping_b
argument_list|(
name|g_s_ending
argument_list|,
literal|98
argument_list|,
literal|116
argument_list|)
operator|)
condition|)
block|{
break|break
name|lab0
break|;
block|}
comment|// delete, line 80
name|slice_del
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
do|while
condition|(
literal|false
condition|)
do|;
name|cursor
operator|=
name|limit
operator|-
name|v_1
expr_stmt|;
comment|// do, line 84
name|v_2
operator|=
name|limit
operator|-
name|cursor
expr_stmt|;
name|lab1
label|:
do|do
block|{
comment|// (, line 84
comment|// [, line 85
name|ket
operator|=
name|cursor
expr_stmt|;
comment|// substring, line 85
name|among_var
operator|=
name|find_among_b
argument_list|(
name|a_2
argument_list|,
literal|4
argument_list|)
expr_stmt|;
if|if
condition|(
name|among_var
operator|==
literal|0
condition|)
block|{
break|break
name|lab1
break|;
block|}
comment|// ], line 85
name|bra
operator|=
name|cursor
expr_stmt|;
comment|// call R1, line 85
if|if
condition|(
operator|!
name|r_R1
argument_list|()
condition|)
block|{
break|break
name|lab1
break|;
block|}
switch|switch
condition|(
name|among_var
condition|)
block|{
case|case
literal|0
case|:
break|break
name|lab1
break|;
case|case
literal|1
case|:
comment|// (, line 87
comment|// delete, line 87
name|slice_del
argument_list|()
expr_stmt|;
break|break;
case|case
literal|2
case|:
comment|// (, line 90
if|if
condition|(
operator|!
operator|(
name|in_grouping_b
argument_list|(
name|g_st_ending
argument_list|,
literal|98
argument_list|,
literal|116
argument_list|)
operator|)
condition|)
block|{
break|break
name|lab1
break|;
block|}
comment|// hop, line 90
block|{
name|int
name|c
init|=
name|cursor
operator|-
literal|3
decl_stmt|;
if|if
condition|(
name|limit_backward
operator|>
name|c
operator|||
name|c
operator|>
name|limit
condition|)
block|{
break|break
name|lab1
break|;
block|}
name|cursor
operator|=
name|c
expr_stmt|;
block|}
comment|// delete, line 90
name|slice_del
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
do|while
condition|(
literal|false
condition|)
do|;
name|cursor
operator|=
name|limit
operator|-
name|v_2
expr_stmt|;
comment|// do, line 94
name|v_3
operator|=
name|limit
operator|-
name|cursor
expr_stmt|;
name|lab2
label|:
do|do
block|{
comment|// (, line 94
comment|// [, line 95
name|ket
operator|=
name|cursor
expr_stmt|;
comment|// substring, line 95
name|among_var
operator|=
name|find_among_b
argument_list|(
name|a_4
argument_list|,
literal|8
argument_list|)
expr_stmt|;
if|if
condition|(
name|among_var
operator|==
literal|0
condition|)
block|{
break|break
name|lab2
break|;
block|}
comment|// ], line 95
name|bra
operator|=
name|cursor
expr_stmt|;
comment|// call R2, line 95
if|if
condition|(
operator|!
name|r_R2
argument_list|()
condition|)
block|{
break|break
name|lab2
break|;
block|}
switch|switch
condition|(
name|among_var
condition|)
block|{
case|case
literal|0
case|:
break|break
name|lab2
break|;
case|case
literal|1
case|:
comment|// (, line 97
comment|// delete, line 97
name|slice_del
argument_list|()
expr_stmt|;
comment|// try, line 98
name|v_4
operator|=
name|limit
operator|-
name|cursor
expr_stmt|;
name|lab3
label|:
do|do
block|{
comment|// (, line 98
comment|// [, line 98
name|ket
operator|=
name|cursor
expr_stmt|;
comment|// literal, line 98
if|if
condition|(
operator|!
operator|(
name|eq_s_b
argument_list|(
literal|2
argument_list|,
literal|"ig"
argument_list|)
operator|)
condition|)
block|{
name|cursor
operator|=
name|limit
operator|-
name|v_4
expr_stmt|;
break|break
name|lab3
break|;
block|}
comment|// ], line 98
name|bra
operator|=
name|cursor
expr_stmt|;
comment|// not, line 98
block|{
name|v_5
operator|=
name|limit
operator|-
name|cursor
expr_stmt|;
name|lab4
label|:
do|do
block|{
comment|// literal, line 98
if|if
condition|(
operator|!
operator|(
name|eq_s_b
argument_list|(
literal|1
argument_list|,
literal|"e"
argument_list|)
operator|)
condition|)
block|{
break|break
name|lab4
break|;
block|}
name|cursor
operator|=
name|limit
operator|-
name|v_4
expr_stmt|;
break|break
name|lab3
break|;
block|}
do|while
condition|(
literal|false
condition|)
do|;
name|cursor
operator|=
name|limit
operator|-
name|v_5
expr_stmt|;
block|}
comment|// call R2, line 98
if|if
condition|(
operator|!
name|r_R2
argument_list|()
condition|)
block|{
name|cursor
operator|=
name|limit
operator|-
name|v_4
expr_stmt|;
break|break
name|lab3
break|;
block|}
comment|// delete, line 98
name|slice_del
argument_list|()
expr_stmt|;
block|}
do|while
condition|(
literal|false
condition|)
do|;
break|break;
case|case
literal|2
case|:
comment|// (, line 101
comment|// not, line 101
block|{
name|v_6
operator|=
name|limit
operator|-
name|cursor
expr_stmt|;
name|lab5
label|:
do|do
block|{
comment|// literal, line 101
if|if
condition|(
operator|!
operator|(
name|eq_s_b
argument_list|(
literal|1
argument_list|,
literal|"e"
argument_list|)
operator|)
condition|)
block|{
break|break
name|lab5
break|;
block|}
break|break
name|lab2
break|;
block|}
do|while
condition|(
literal|false
condition|)
do|;
name|cursor
operator|=
name|limit
operator|-
name|v_6
expr_stmt|;
block|}
comment|// delete, line 101
name|slice_del
argument_list|()
expr_stmt|;
break|break;
case|case
literal|3
case|:
comment|// (, line 104
comment|// delete, line 104
name|slice_del
argument_list|()
expr_stmt|;
comment|// try, line 105
name|v_7
operator|=
name|limit
operator|-
name|cursor
expr_stmt|;
name|lab6
label|:
do|do
block|{
comment|// (, line 105
comment|// [, line 106
name|ket
operator|=
name|cursor
expr_stmt|;
comment|// or, line 106
name|lab7
label|:
do|do
block|{
name|v_8
operator|=
name|limit
operator|-
name|cursor
expr_stmt|;
name|lab8
label|:
do|do
block|{
comment|// literal, line 106
if|if
condition|(
operator|!
operator|(
name|eq_s_b
argument_list|(
literal|2
argument_list|,
literal|"er"
argument_list|)
operator|)
condition|)
block|{
break|break
name|lab8
break|;
block|}
break|break
name|lab7
break|;
block|}
do|while
condition|(
literal|false
condition|)
do|;
name|cursor
operator|=
name|limit
operator|-
name|v_8
expr_stmt|;
comment|// literal, line 106
if|if
condition|(
operator|!
operator|(
name|eq_s_b
argument_list|(
literal|2
argument_list|,
literal|"en"
argument_list|)
operator|)
condition|)
block|{
name|cursor
operator|=
name|limit
operator|-
name|v_7
expr_stmt|;
break|break
name|lab6
break|;
block|}
block|}
do|while
condition|(
literal|false
condition|)
do|;
comment|// ], line 106
name|bra
operator|=
name|cursor
expr_stmt|;
comment|// call R1, line 106
if|if
condition|(
operator|!
name|r_R1
argument_list|()
condition|)
block|{
name|cursor
operator|=
name|limit
operator|-
name|v_7
expr_stmt|;
break|break
name|lab6
break|;
block|}
comment|// delete, line 106
name|slice_del
argument_list|()
expr_stmt|;
block|}
do|while
condition|(
literal|false
condition|)
do|;
break|break;
case|case
literal|4
case|:
comment|// (, line 110
comment|// delete, line 110
name|slice_del
argument_list|()
expr_stmt|;
comment|// try, line 111
name|v_9
operator|=
name|limit
operator|-
name|cursor
expr_stmt|;
name|lab9
label|:
do|do
block|{
comment|// (, line 111
comment|// [, line 112
name|ket
operator|=
name|cursor
expr_stmt|;
comment|// substring, line 112
name|among_var
operator|=
name|find_among_b
argument_list|(
name|a_3
argument_list|,
literal|2
argument_list|)
expr_stmt|;
if|if
condition|(
name|among_var
operator|==
literal|0
condition|)
block|{
name|cursor
operator|=
name|limit
operator|-
name|v_9
expr_stmt|;
break|break
name|lab9
break|;
block|}
comment|// ], line 112
name|bra
operator|=
name|cursor
expr_stmt|;
comment|// call R2, line 112
if|if
condition|(
operator|!
name|r_R2
argument_list|()
condition|)
block|{
name|cursor
operator|=
name|limit
operator|-
name|v_9
expr_stmt|;
break|break
name|lab9
break|;
block|}
switch|switch
condition|(
name|among_var
condition|)
block|{
case|case
literal|0
case|:
name|cursor
operator|=
name|limit
operator|-
name|v_9
expr_stmt|;
break|break
name|lab9
break|;
case|case
literal|1
case|:
comment|// (, line 114
comment|// delete, line 114
name|slice_del
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
do|while
condition|(
literal|false
condition|)
do|;
break|break;
block|}
block|}
do|while
condition|(
literal|false
condition|)
do|;
name|cursor
operator|=
name|limit
operator|-
name|v_3
expr_stmt|;
return|return
literal|true
return|;
block|}
DECL|method|stem
specifier|public
name|boolean
name|stem
parameter_list|()
block|{
name|int
name|v_1
decl_stmt|;
name|int
name|v_2
decl_stmt|;
name|int
name|v_3
decl_stmt|;
name|int
name|v_4
decl_stmt|;
comment|// (, line 124
comment|// do, line 125
name|v_1
operator|=
name|cursor
expr_stmt|;
name|lab0
label|:
do|do
block|{
comment|// call prelude, line 125
if|if
condition|(
operator|!
name|r_prelude
argument_list|()
condition|)
block|{
break|break
name|lab0
break|;
block|}
block|}
do|while
condition|(
literal|false
condition|)
do|;
name|cursor
operator|=
name|v_1
expr_stmt|;
comment|// do, line 126
name|v_2
operator|=
name|cursor
expr_stmt|;
name|lab1
label|:
do|do
block|{
comment|// call mark_regions, line 126
if|if
condition|(
operator|!
name|r_mark_regions
argument_list|()
condition|)
block|{
break|break
name|lab1
break|;
block|}
block|}
do|while
condition|(
literal|false
condition|)
do|;
name|cursor
operator|=
name|v_2
expr_stmt|;
comment|// backwards, line 127
name|limit_backward
operator|=
name|cursor
expr_stmt|;
name|cursor
operator|=
name|limit
expr_stmt|;
comment|// do, line 128
name|v_3
operator|=
name|limit
operator|-
name|cursor
expr_stmt|;
name|lab2
label|:
do|do
block|{
comment|// call standard_suffix, line 128
if|if
condition|(
operator|!
name|r_standard_suffix
argument_list|()
condition|)
block|{
break|break
name|lab2
break|;
block|}
block|}
do|while
condition|(
literal|false
condition|)
do|;
name|cursor
operator|=
name|limit
operator|-
name|v_3
expr_stmt|;
name|cursor
operator|=
name|limit_backward
expr_stmt|;
comment|// do, line 129
name|v_4
operator|=
name|cursor
expr_stmt|;
name|lab3
label|:
do|do
block|{
comment|// call postlude, line 129
if|if
condition|(
operator|!
name|r_postlude
argument_list|()
condition|)
block|{
break|break
name|lab3
break|;
block|}
block|}
do|while
condition|(
literal|false
condition|)
do|;
name|cursor
operator|=
name|v_4
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
end_class
end_unit
