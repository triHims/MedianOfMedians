# MedianOfMedians java implmementation
Storing research for medianOfMedians. 
NOTE , i am just trying to learn the algorithm. 

I have found this answer very helpful in trying to implment the algorithm
saving it here because i don't trust internet anymore. Things get deleted.
https://stackoverflow.com/a/52471681


Note: this implmentation falls to n2 on extremly skewed inputs


## Answer - credits: user m69 ''snarky and unwelcoming'' https://stackoverflow.com/users/4907604/m69-snarky-and-unwelcoming
The cause of your confusion about the median-of-medians algorithm is that, while median-of-medians returns an approximate result within 20% of the actual median, at some stages in the algorithm we also need to calculate exact medians. If you mix up the two, you will not get the expected result, as demonstrated in your example.

Median-of-medians uses three functions as its building blocks:
```java
medianOfFive(array, first, last) {
    // ...
    return median;
}
```

This function returns the exact median of five (or fewer) elements from (part of) an array. There are several ways to code this, based on e.g. a sorting network or insertion sort. The details are not important for this question, but it is important to note that this function returns the exact median, not an approximation.

```
medianOfMedians(array, first, last) {
    // ...
    return median;
}
```

This function returns an approximation of the median from (part of) an array, which is guaranteed to be larger than the 30% smallest elements, and smaller than the 30% largest elements. We'll go into more detail below.

```
select(array, first, last, n) {
    // ...
    return element;
}
```

This function returns the n-th smallest element from (part of) an array. This function too returns an exact result, not an approximation.

At its most basic, the overall algorithm works like this:

```
medianOfMedians(array, first, last) {
    call medianOfFive() for every group of five elements
    fill an array with these medians
    call select() for this array to find the middle element
    return this middle element (i.e. the median of medians)
}
```

So this is where your calculation went wrong. After creating an array with the median-of-fives, you then used the median-of-medians function again on this array, which gives you an approximation of the median (27), but here you need the actual median (1038).

This all sounds fairly straightforward, but where it becomes complicated is that the function select() calls medianOfMedians() to get a first estimate of the median, which it then uses to calculate the exact median, so you get a two-way recursion where two functions call each other. This recursion stops when medianOfMedians() is called for 25 elements or fewer, because then there are only 5 medians, and instead of using select() to find their median, it can use medianOfFive().

The reason why select() calls medianOfMedians() is that it uses partitioning to split (part of) the array into two parts of close to equal size, and it needs a good pivot value to do that. After it has partitioned the array into two parts with the elements which are smaller and larger than the pivot, it then checks which part the n-th smallest element is in, and recurses with this part. If the size of the part with the smaller values is n-1, the pivot is the n-th value, and no further recursion is needed.

```
select(array, first, last, n) {
    call medianOfMedians() to get approximate median as pivot
    partition (the range of) the array into smaller and larger than pivot
    if part with smaller elements is size n-1, return pivot
    call select() on the part which contains the n-th element
}
```

As you see, the select() function recurses (unless the pivot happens to be the n-th element), but on ever smaller ranges of the array, so at some point (e.g. two elements) finding the n-th element will become trivial, and recursing further is no longer needed.

So finally we get, in some more detail:

```
medianOfFive(array, first, last) {
    // some algorithmic magic ...
    return median;
}

medianOfMedians(array, first, last) {
    if 5 elements or fewer, call medianOfFive() and return result
    call medianOfFive() for every group of five elements
    store the results in an array medians[]
    if 5 elements or fewer, call medianOfFive() and return result
    call select(medians[]) to find the middle element
    return the result (i.e. the median of medians)
}

select(array, first, last, n) {
    if 2 elements, compare and return n-th element
    if 5 elements or fewer, call medianOfFive() to get median as pivot
    else call medianOfMedians() to get approximate median as pivot
    partition (the range of) the array into smaller and larger than pivot
    if part with smaller elements is size n-1, return pivot
    if n-th value is in part with larger values, recalculate value of n
    call select() on the part which contains the n-th element
}
```

## EXAMPLE

Input array (125 values, 25 groups of five):

```

 #1    #2    #3    #4    #5    #6    #7    #8    #9    #10   #11   #12   #13   #14   #15   #16   #17   #18   #19   #20   #21   #22   #23   #24   #25

   1     4     7  1020  1025    10    13    16  1029  1036    19    22    25  1041  1046  1051  1056  1061  1066  1071  1076  1081  1086  1091  1096
   2     5     8  1021  1026    11    14    17  1030  1037    20    23    26  1042  1047  1052  1057  1062  1067  1072  1077  1082  1087  1092  1097
   3     6     9  1022  1027    12    15    18  1031  1038    21    24    27  1043  1048  1053  1058  1063  1068  1073  1078  1083  1088  1093  1098
1001  1003  1005  1023  1028  1007  1009  1011  1032  1039  1014  1016  1018  1044  1049  1054  1059  1064  1069  1074  1079  1084  1089  1094  1099
1002  1004  1006  1034  1035  1008  1010  1013  1033  1040  1015  1017  1019  1045  1050  1055  1060  1065  1070  1075  1080  1085  1090  1095  1100
```

Medians of groups of five (25 values):

```
3, 6, 9, 1022, 1027, 12, 15, 18, 1031, 1038, 21, 24, 27, 1043,  
1048, 1053, 1058, 1063, 1068, 1073, 1078, 1083, 1088, 1093, 1098
```

Groups of five for approximate median:

```

 #1    #2    #3    #4    #5

   3    12    21  1053  1078
   6    15    24  1058  1083
   9    18    27  1063  1088
1022  1031  1043  1068  1096
1027  1038  1048  1073  1098
```

Medians of five for approximate median:

```
9, 18, 27, 1063, 1088
```

Approximate median as pivot:

```
27
```

Medians of five partitioned with pivot 27 (depends on method):

```
small: 3, 6, 9, 24, 21, 12, 15, 18
pivot: 27
large: 1031, 1038, 1027, 1022, 1043, 1048, 1053, 1058,  
       1063, 1068, 1073, 1078, 1083, 1088, 1093, 1098
```

The smaller group has 8 elements, the larger group 16 elements. We were looking for the middle 13th element out of 25, so now we look for the 13 - 8 - 1 = 4th element out of 16:

Groups of five:

```
 #1    #2    #3    #4

1031  1048  1073  1098
1038  1053  1078
1027  1058  1083
1022  1063  1088
1043  1068  1093
```

Medians of groups of five:

1031, 1058, 1083, 1098

Approximate median as pivot:

```
1058
```

Range of medians of five partitioned with pivot 1058 (depends on method):

```
small: 1031, 1038, 1027, 1022, 1043, 1048, 1053
pivot: 1058
large: 1063, 1068, 1073, 1078, 1083, 1088, 1093, 1098
```

The smaller group has 7 elements. We were looking for the 4th element of 16, so now we look for the 4th element out of 7:

Groups of five:

```
 #1    #2

1031  1048
1038  1053
1027
1022
1043
```

Medians of groups of five:

```
1031, 1048
```

Approximate median as pivot:

```
1031
```

Range of medians of five partitioned with pivot 1031 (depends on method):

```
small: 1022, 1027
pivot: 1031
large: 1038, 1043, 1048, 1053
```

The smaller part has 2 elements, and the larger has 4, so now we look for the 4 - 2 - 1 = 1st element out of 4:

Median of five as pivot:

```
1043
```

Range of medians of five partitioned with pivot 1043 (depends on method):

```
small: 1038
pivot: 1043
large: 1048, 1053
```

The smaller part has only one element, and we were looking for the first element, so we can return the small element 1038.

As you will see, 1038 is the exact median of the original 25 median-of-fives, and there are 62 smaller values in the original array of 125:

```
1 ~ 27, 1001 ~ 1011, 1013 ~ 1023, 1025 ~ 1037
```

which not only puts it in the 30~70% range, but means it is actually the exact median (note that this is a coincidence of this particular example).
  
