package Tools;

/**
 * Mutable, signed <em>rational</em> decimal numbers.It's implemented based on
 * the native long type, so you need to pay attention to scope when you use it.
 * Use it's brother, the {@code BigFraction} class, when you need bigger scope.
 * 
 * @see		BigFraction
 * @author	Tentacle
 * @version	0.01
 */

public class Fraction implements Comparable<Fraction>,Cloneable {
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
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		Fraction c = (Fraction) super.clone();
		c.numerator = this.numerator;
		c.denominator = this.denominator;
		c.sign = this.sign;
		return c;
	}
	
	public Fraction copy() {
		return new Fraction(this);
	}
	
	public Fraction add(Fraction another) throws Exception {
		long num = this.sign*this.numerator*another.denominator + another.sign*another.numerator*this.denominator;
		long den = this.denominator*another.denominator;
		return new Fraction(num,den);
	}
	public Fraction add(long n, long d) throws Exception {
		long num = this.sign*this.numerator*((d>0)?d:-d) + ((n*d>0)?1:-1)*((n>0)?n:-n)*this.denominator;
		long den = this.denominator*((d>0)?d:-d);
		return new Fraction(num,den);
	}
	public Fraction add(long anotherNum) throws Exception {
		long num = this.sign*this.numerator + anotherNum*this.denominator;
		long den = this.denominator;
		return new Fraction(num,den);
	}
	
	public Fraction sub(Fraction another) throws Exception {
		return add(another.sign==1?-another.numerator:another.numerator,another.denominator);
	}
	public Fraction sub(long n, long m) throws Exception {
		return add(-n, m);
	}
	public Fraction sub(long anotherNum) throws Exception {
		return add(-anotherNum);
	}
	
	public Fraction multiply(Fraction another) throws Exception {
		long num = this.sign*another.sign*this.numerator*another.numerator;
		long den = this.denominator*another.denominator;
		return new Fraction(num,den);
	}
	public Fraction multiply(long n , long d) throws Exception {
		long num = this.sign*this.numerator*n;
		long den = this.denominator*d;
		return new Fraction(num,den);
	}
	public Fraction multiply(long anotherNum) throws Exception {
		long num = this.sign*this.numerator*anotherNum;
		long den = this.denominator;
		return new Fraction(num,den);
	}
	
	public Fraction divide(Fraction another) throws Exception {
		return multiply(another.denominator, another.numerator*another.sign);
	}
	public Fraction divide(long n , long d) throws Exception {
		return multiply(d, n);
	}
	public Fraction divide(long anotherNum) throws Exception {
		long num = this.sign*this.numerator;
		long den = this.denominator*anotherNum;
		return new Fraction(num,den);
	}
	
	public Fraction remainder(Fraction another) throws Exception {
		long num = this.sign*this.numerator*another.denominator*another.sign;
		long den = this.denominator*another.numerator;
		return this.add(-(num/den*another.numerator*another.sign), another.denominator);
	}
	public Fraction remainder(long n , long d) throws Exception {
		long num = this.sign*this.numerator*d;
		long den = this.denominator*n;
		return this.add(-(num/den*n), d);
	}
	public Fraction remainder(long anotherNum) throws Exception {
		long num = this.sign*this.numerator;
		long den = this.denominator*anotherNum;
		return this.add(-(num/den*anotherNum), 1);
	}
	
	public Fraction sqrt() throws Exception {
		if(this.sign == 1) {	
			return new Fraction((long)StrictMath.sqrt(this.numerator), (long)StrictMath.sqrt(this.denominator));
		}else {
			return null;
		}
	}
	
	public Fraction power(long pow) throws Exception {
		if(pow == 0) {
			return new Fraction(1,1);
		}
		boolean powSg = pow>0?true:false;
		pow = powSg?pow:-pow;
		int sg = ((pow&1)==1?this.sign:1);
		long resNum = 1,resDen = 1;
		long baseNum = this.numerator,baseDen = this.denominator;
		for(; pow!=0 ; pow>>=1) {
			if((pow&1)==1) {
				resNum*=baseNum;resDen*=baseDen;
			}
			baseNum*=baseNum;baseDen*=baseDen;
		}
		resNum*=sg;
		return powSg?new Fraction(resNum,resDen):new Fraction(resDen,resNum);
	}
	public Fraction power(double pow) throws Exception {
		if(pow>=0) {
			if(pow<1&&this.sign==-1) {
				return null;
			}
			return new Fraction((long)StrictMath.pow(this.numerator*this.sign, pow), (long)StrictMath.pow(this.denominator, pow));
		}else {
			if(pow>-1&&this.sign==-1) {
				return null;
			}
			return new Fraction((long)StrictMath.pow(this.denominator, -pow), (long)StrictMath.pow(this.numerator*this.sign, -pow));
		}
	}
	public Fraction power(Fraction pow) throws Exception {
		return this.power(pow.toDouble());
	}
	public Fraction power(long n , long d) throws Exception {
		return this.power(1.0*n/d);
	}
	
	public Fraction abs() throws Exception {
		return (this.sign==1)?this:this.negate();
	}
	
	public Fraction negate() throws Exception {
		return new Fraction(this.sign>0?-this.numerator:this.numerator , this.denominator);
	}
	
	public Fraction plus() {
		return new Fraction(this);
	}
	
	public Fraction addSelf(Fraction another) throws Exception {
		this.numerator = this.sign*this.numerator*another.denominator + another.sign*another.numerator*this.denominator;
		this.denominator = this.denominator * another.denominator;
		this.simplify();
		return this;
	}
	public Fraction addSelf(long n, long d) throws Exception {
		this.numerator = this.sign*this.numerator*((d>0)?d:-d) + ((n*d>0)?1:-1)*((n>0)?n:-n)*this.denominator;
		this.denominator = this.denominator*((d>0)?d:-d);
		this.simplify();
		return this;
	}
	public Fraction addSelf(long anotherNum) throws Exception {
		this.numerator = this.sign*this.numerator + anotherNum*this.denominator;
		this.simplify();
		return this;
	}

	public Fraction subSelf(Fraction another) throws Exception {
		return this.addSelf((another.sign==1?-another.numerator:another.numerator),another.denominator);
	}
	public Fraction subSelf(long n, long m) throws Exception {
		return this.addSelf(-n, m);
	}
	public Fraction subSelf(long anotherNum) throws Exception {
		return this.addSelf(-anotherNum);
	}

	public Fraction multiplySelf(Fraction another) throws Exception {
		this.numerator *= this.sign*another.sign*another.numerator;
		this.denominator *= another.denominator;
		this.simplify();
		return this;
	}
	public Fraction multiplySelf(long n, long d) throws Exception {
		this.numerator *= this.sign*((n*d>0)?1:-1)*((n>0)?n:-n);
		this.denominator *= ((d>0)?d:-d);
		this.simplify();
		return this;
	}
	public Fraction multiplySelf(long anotherNum) throws Exception {
		this.numerator *= this.sign*anotherNum;
		this.simplify();
		return this;
	}

	public Fraction divideSelf(Fraction another) throws Exception {
		return multiplySelf(another.sign*another.denominator , another.numerator);
	}
	public Fraction divideSelf(long n, long d) throws Exception {
		return multiply(d, n);
	}
	public Fraction divideSelf(long anotherNum) throws Exception {
		this.numerator *= this.sign*(anotherNum>=0?1:-1);
		this.denominator *= (anotherNum>=0?anotherNum:-anotherNum);
		this.simplify();
		return this;
	}
	
	public Fraction remainderSelf(Fraction another) throws Exception {
		long num = this.sign*this.numerator*another.denominator*another.sign;
		long den = this.denominator*another.numerator;
		return this.addSelf(-(num/den*another.numerator*another.sign), another.denominator);
	}
	public Fraction remainderSelf(long n , long d) throws Exception {
		long num = this.sign*this.numerator*d;
		long den = this.denominator*n;
		return this.addSelf(-(num/den*n), d);
	}
	public Fraction remainderSelf(long anotherNum) throws Exception {
		long num = this.sign*this.numerator;
		long den = this.denominator*anotherNum;
		return this.addSelf(-(num/den*anotherNum), 1);
	}
	
	public Fraction sqrtSelf() throws Exception {
		if(this.sign == 1) {	
			this.numerator = (long)StrictMath.sqrt(this.numerator);
			this.denominator = (long)StrictMath.sqrt(this.denominator);
			return this;
		}else {
			return null;
		}
	}
	
	public Fraction powerSelf(long pow) throws Exception {
		if(pow == 0) {
			this.numerator = 1;this.denominator = 1;this.sign = 1;
			return this;
		}
		boolean powSg = pow>0?true:false;
		pow = powSg?pow:-pow;
		this.sign = ((pow&1)==1?this.sign:1);
		long resNum = 1,resDen = 1;
		long baseNum = this.numerator,baseDen = this.denominator;
		for(; pow!=0 ; pow>>=1) {
			if((pow&1)==1) {
				resNum*=baseNum;resDen*=baseDen;
			}
			baseNum*=baseNum;baseDen*=baseDen;
		}
		if(powSg) {
			this.numerator = resNum;this.denominator = resDen;
		}else {
			this.numerator = resDen;this.denominator = resNum;			
		}
		this.simplify();
		return this;
	}
	public Fraction powerSelf(double pow) throws Exception {
		if(pow>=0) {
			if(pow<1&&this.sign==-1) {
				return null;
			}
			this.numerator = (long)StrictMath.pow(this.numerator*this.sign, pow);
			this.denominator = (long)StrictMath.pow(this.denominator, pow);
			this.sign = this.numerator<0?-1:1;
			this.numerator*=this.sign;
			return this;
		}else {
			if(pow>-1&&this.sign==-1) {
				return null;
			}
			long tmp = this.numerator;
			this.numerator = (long)StrictMath.pow(this.denominator*this.sign, -pow);
			this.denominator = (long)StrictMath.pow(tmp, -pow);
			this.sign = this.numerator<0?-1:1;
			this.numerator*=this.sign;
			return this;
		}
	}
	public Fraction powerSelf(Fraction pow) throws Exception {
		return this.powerSelf(pow.toDouble());
	}
	public Fraction powerSelf(long n , long d) throws Exception {
		return this.powerSelf(1.0*n/d);
	}
	
	public Fraction absSelf() {
		this.sign = 1;
		return this;
	}
	
	public Fraction negateSelf() {
		this.sign = -this.sign;
		return this;
	}
	
	public Fraction plusSelf() {
		return this;
	}
	
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
}
