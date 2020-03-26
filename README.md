# 一个实用分数类Fraction的Java实现

### 关于本类的简要说明

本Fraction类，结构和用法类似于java.math中提供的BigInteger和BigDecimal类，如果你曾使用或了解过这两个类型，那么对于本类，你同样能很快熟悉，它也远比那两个类要简单，但有以下两点主要区别：

1.本Fraction是**可变的(mutable)**，并为提升此特性的意义，对本类中所有的计算相关方法都设置了两组，从特性上可分为**产生新对象的** 和**不产生新对象的**。例如，本类中，对于add方法，将会有名为add和addSelf两组，顾名思义，后者的计算仅在本对象内完成，过程中**完全**不会出现任何对象创建，xxxSelf的方法全为对自身改动，两组方法在使用上的区别类似于"+"运算符与"+="运算符的区别。因此，在程序的需求允许使用Self方法时，尽量的使用Self方法，理论上可以**提高效率**。

2.本Fraction类基于基本的**long类型**，因此，本类对数字的大小有所限制，同时，因本类的某些方法中包含对本类储值变量的左位移，因此，本类的实际允许大小应为**long类型的一半**。

### 代码内容

+ #####  成员变量

  > 这部分其实也没什么好说的，就三部分**符号(sign)**、**分子(numerator)**、**分母(denominator)**。
  >
  > 小朋友，也许细心的你已经发现力，我居然还写了点元素注释，嗯......是打算写来着，实在是后面东西有点多，懒得写了，就这了，没了。

```java
/**
  * The sign of this Fraction: -1 for negative or 1 for positive. 
  * Note that the Fraction zero have a sign of 1, which is doesn't
  * like the <code>BigInteger</code> and the <code>BigDecimal</code>.
  */
private int sign;
/**
  * The unsigned numerator value of this Fraction, but there is
  * exception. It'll be signed when taking part in calculating.
  */
private long numerator;
/**
  * The unsigned denominator value of this Fraction, it's strict
  * unsigned at any time after been created.
  */
private long denominator;
```

+ #####  构造方法

  > 共提供了4个构造方法，分别允许以传入**分子分母**、**整数**、**字符串**的方式来创建Fraction对象，并提供一个复制用构造方法。构造方法这一部分还并**未完善**，因急于使用，并未做传入内容的合法检测，包括传入long的大小和String的详细内容。

```java
public Fraction(long num, long den) throws Exception{
    if (den == 0) {
        throw new Exception("Denominator cannot be zero.");
    }
    sign = num*den >= 0 ? 1 : -1;
    this.numerator = num>0?num:-num;
    this.denominator = den>0?den:-den;
    long gcd = getGDC(numerator, denominator);
    this.numerator /= gcd;
    this.denominator /= gcd;
}

public Fraction(long num) throws Exception {
    this(num,1);
}

public Fraction(String fraction) throws NumberFormatException, Exception {
    this(	Long.parseLong(fraction.substring(0, fraction.indexOf('/'))),
         	Long.parseLong(fraction.substring(	fraction.indexOf('/')+1,
                                           		fraction.length()))		);
}

public Fraction(Fraction copy) {
    this.sign = copy.sign;
    this.numerator = copy.numerator;
    this.denominator = copy.denominator;
}
```

+ #####  计算相关方法

  > 这部分过于繁琐且重复性高，就不详细给出，这里只列出本类所实现的方法名，具体内容就不写这里了。xxxSelf的方法也不在这写了，形式上是一样的。

```java
//加法
public Fraction add(Fraction another);//与另一分数相加(下同)
public Fraction add(long n, long d);//传入两个long分别表示另一分数的分子和分母(下同)
public Fraction add(long anotherNum);//与另一整数相加(下同)
//减法
public Fraction sub(Fraction another);
public Fraction sub(long n, long m);
public Fraction sub(long anotherNum);
//乘法
public Fraction multiply(Fraction another);
public Fraction multiply(long n , long d);
public Fraction multiply(long anotherNum);
//除法
public Fraction divide(Fraction another);
public Fraction divide(long n , long d);
public Fraction divide(long anotherNum);
//取余
public Fraction remainder(Fraction another);
public Fraction remainder(long n , long d);
public Fraction remainder(long anotherNum);
//开方
public Fraction sqrt()//由于是对整数开方，所以结果常会出现不准确的情况，如对1/8开方将得1/2
//幂运算
public Fraction power(double pow);//通过调用StrictMath.pow简单实现的幂运算，允许实数传入
public Fraction power(long pow);//此方法内部采用快速幂，并未调用java库函数,在数据较小的情况下经测试远快于上一方法，得益于分数的特性，该方法能够很方便的实现对负指数的快速幂，允许整数传入
public Fraction power(Fraction pow);//调用了power(double)
public Fraction power(long n , long d);//调用了power(double)
//取绝对值
public Fraction abs();
//取反
public Fraction negate();
//取同
public Fraction plus();
```

+ #####  属性set/get

  > 这部分也没啥好说的。

```java
public int signum() {
    return this.numerator == 0?0:this.sign;
}
public void setSignum(int sign) {
    if(sign == 0) {
        this.numerator = 0;
        this.denominator = 1;
        this.sign = 1;
    }else if(sign/2==0) {
        this.sign = sign;
    }
}

public long Numerator() {
    return this.numerator;
}
public void setNumerator(long num) throws Exception {
    this.numerator = num;
    this.simplify();
}

public long Denominator() {
    return this.denominator;
}
public void setDenominator(long den) throws Exception {
    this.denominator = den;
    this.simplify();
}
```

+ #####  比较用方法

  > 本类实现了Comparable接口，并重写了Object的equals方法，此外，还准备了一个用于直接与整数比较的方法，不过好像没啥用。

```java
@Override
public boolean equals(Object x) {
	if (!(x instanceof Fraction))
		return false;
	Fraction xFra = (Fraction) x;
	if (x == this)
		return true;
	if (this.sign != xFra.sign || this.numerator != xFra.numerator || this.denominator != xFra.denominator )
		return false;
	return true;
}
public boolean equals(long num) {
    if (this.denominator != 1)
        return false;
    if(this.numerator == num) {
        return true;
    }else {
        return false;
    }
}

@Override
public int compareTo(Fraction o) {
    if(this.denominator == o.denominator) {
        if(this.numerator*this.sign > o.numerator*o.sign) {
            return 1;
        }else if(this.numerator*this.sign < o.numerator*o.sign) {
            return -1;
        }else {
            return 0;
        }
    }else{
        return (this.toDouble() > o.toDouble())?1:-1;
    }
}
```

+ #####  转型方法

  > 共3类转型方法，可将Fraction分数对象转化为**整数(long)**、**浮点数(double)**，以及对Object的toString重写，将本对象转化为**字符串(String)**。其中化整方法包含**向上/下取整**和**四舍五入**。

```java
public final long floor() {
    return this.numerator*this.sign/this.denominator-(this.sign==-1?1:0);
}

public final long ceil() {
    return this.numerator*this.sign/this.denominator+(this.sign==1?1:0);
}

public final long round() {
    return ((this.numerator<<1)+this.denominator)/(this.denominator<<1)*this.sign;
}

public double toDouble() {
    return 1.0*this.sign*this.numerator/this.denominator;
}

@Override
public String toString() {
    if (this.numerator == 0)return "0";
    if(this.denominator == 1)return (this.sign == 1 ? "" : "-") + this.numerator;
    return (this.sign == 1 ? "" : "-") + this.numerator + "/" + this.denominator;
}
```

+ #####  私有工具方法

  > 本类共用到两个工具方法，两个方法共同完成一个功能，分数的化简。其中包含一个求最大公约数的方法，算是本类中唯一有一些动脑筋的地方，基本原理是**更相减损**，但在其基础上有进一步优化，并且为节省内存并未采用递归，在速度上，较穷举自然是快了不知多少，不知是否还有更好的方法。

```java
private void simplify() throws Exception {
	if(this.numerator == 0) {
		this.denominator = 1;
		this.sign = 1;
		return;
	}
	if(this.denominator==0) {
		throw new Exception("Denominator cannot be zero.");
	}
	this.sign = numerator<0?-1:1;
	this.numerator = this.numerator>0?this.numerator:-this.numerator;
	long gcd = getGDC(numerator, denominator);
	this.numerator /= gcd;
	this.denominator /= gcd;
}

private final long getGDC(long a, long b) {
	if(a==0||b==0)return 1;
	long resScale = 0;
	for(;true;) {
		if(a%b==0) {
			return b<<resScale;
		}else if(b%a==0) {
			return a<<resScale;
		}
		if((a&1)==0 && (b&1)==0) {
			a>>=1;b>>=1;resScale++;
		}else if((a&1)==0) {
			a>>=1;
		}else if((b&1)==0) {
			b>>=1;
		}else {
			if(a>b) {
				a=a-b;
			}else {
				b=b-a;
			}
		}
	}
}
```
