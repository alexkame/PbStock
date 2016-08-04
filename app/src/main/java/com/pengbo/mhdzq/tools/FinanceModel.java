package com.pengbo.mhdzq.tools;

public class FinanceModel {
	
	public	static final double MAX_PRICE_PROBABILITY =  9999;
	public	static final double MIN_PRICE_PROBABILITY = 0.001;
	
	public	static final int MAX_BTREE_STEPS =	30;
	public	static final int PROFITANALYSE_TOTAL_COUNT  =  81;   //计算估算损益比的价格变动档位总数,以现价为中间点，高低各40个点
	public	static final double PROFITANALYSE_PRICE_RATE =   0.02; //每挡价格变动百分比，0.02相当于2%
	
	public	static final int OPTION_CALL= 0 ; //认沽期权
	public	static final int OPTION_PUT = 1;  //认购期权
	
	public	static final int OPTION_STOCK = 0;
	public	static final int OPTION_INDEX = 1;
	public	static final int OPTION_FUTURE = 2;
	
	public	static int dayofwk(int yy, int mm, int dd)
	{
		mm -= 2;
		if( mm<=0 )	
		{ 
			mm += 12;	
			yy--; 
		}
		int temp = yy/100;
		yy %= 100;
		return (((13*mm-1)/5+dd+yy+yy/4+temp/4-temp-temp)%7+7)%7;
	}

	//计算两个日期之间的天数
	//y1=开始年份YYYY
	public	static int TimeDruation2Days(int y1, int m1, int d1, int y2, int m2, int d2) //忽略2月29日,不倒数日期
	{
		int []mds = new int[]{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
		
		if (y1<1900 || y2<1900 || m1<1 || m2<1 || m1>12 || m2>12 || d1<1 || d2<1)
			return 0;
		if (m1==2 && d1==29)
			d1 = 28;
		if (m2==2 && d2==29)
			d2 = 28;
		if (mds[m1-1]<d1 || mds[m2-1]<d2)
			return 0;

		int days1=(y1-1900)*365+d1;
		int days2=(y2-1900)*365+d2;
		int i;
		for (i=1; i<m1; i++)
			days1 += mds[i-1];
		for (i=1; i<m2; i++)
			days2 += mds[i-1];

		return ((days2>days1) ? (days2-days1) : 0);
	}

	//计算两个日期之间的年数
	//y1=开始年份YYYY
	public	static double TimeDruation2Years(int y1, int m1, int d1, int y2, int m2, int d2)
	{
		int dif=TimeDruation2Days(y1, m1, d1, y2, m2, d2);

		//日历日差值要转化为工作日差值
		if (dif>0)
		{
			int wk1=dayofwk(y1, m1, d1);
			int wk2=dayofwk(y2, m2, d2);
			int wks=dif/7;
			if (wk2>=wk1)
				dif = wks*5 + wk2 - wk1;
			else
				dif = wks*5 + wk2 - wk1 + 5;
		}

		return ((double)dif / 260.0);
	}

	//计算正态分布密度函数值N'(x)
	public	static double NormsDistDensity(double x)
	{
		return Math.exp(-x*x/2)/ Math.sqrt(2*3.141592653589);
	}

	//计算正态分布函数值N(x)
	public	static double NormsDistValue(double x)
	{
		if (x<0)
			return (1.0 - NormsDistValue(-x));

		double a1=0.319381530;
		double a2=-0.356563782;
		double a3=1.781477937;
		double a4=-1.821255978;
		double a5=1.330274429;
		double k=1.0/(1.0+0.2316419*x);

		return (1.0 - Math.exp(-x*x/2)/Math.sqrt(2*3.141592653589)*(a1*k+a2*k*k+a3*Math.pow(k,3)+a4*Math.pow(k,4)+a5*Math.pow(k,5)));
	}

//	//计算数学期望
//	double GetAverage(double *list, int count)
//	{
//		double sum=0.0;
//		for (int i=0; i<count; i++)
//			sum += list[i];
//		return (count>0 ? sum/count : 0);
//	}
//
//	//计算历史波动率
//	double GetVolatility(double *p, int count)
//	{
//		if (count<5) //数据太少不值得计算
//			return 0;
//
//		int i;
//		double m=0.0;
//		double y1=0.0, y2=0.0;
//
//		for (i=1; i<count; i++)
//		{
//			m = p[i-1]>0 ? Math.log(p[i]/p[i-1]) : 0;
//			y1 += m;
//			y2 += m*m;
//		}
//
//		count--;
//		double d1_2 = y2/(count-1) - y1*y1/count/(count-1);
//
//		//按一年250个交易日计算
//		return Math.sqrt(d1_2 * 250);
//	}

	//用B-S模型计算一个欧式期权的理论价格，股票期权和股指期权的计算中都不考虑股息影响
	//direct=权证方向，0-CALL,1-PUT,type=权证类型0-股票期权，1-股指期权，2-期货期权
	//stockprice=当前标的物价格，strikeprice=权证行权价格，
	//volatility=标的物历史波动率，r=无风险利率，t=期权期限，以年为单位
	//返回值：期权理论价格，<0表示计算失败
	public	static double GetOptionBSPrice(int direct,int type, double stockprice, double strikeprice, double volatility, double r, double t)
	{
		double sp=-1.0;
		double d1,d2;

		if (stockprice<=0 || strikeprice<=0 || t<=0 || volatility<=0)
			return -1.0;

		if (type!=OPTION_STOCK) //股指期权和期货期权模型相同
		{
			d1 = (Math.log(stockprice/strikeprice) + volatility*volatility*t/2) / (volatility*Math.sqrt(t));
			d2 = d1 -volatility*Math.sqrt(t);
			if (direct!=OPTION_CALL) //认沽期权
			{
				sp = Math.exp(-r*t)*(strikeprice*NormsDistValue(-d2) - stockprice*NormsDistValue(-d1));
			}
			else //认购期权
			{
				sp = Math.exp(-r*t)*(stockprice*NormsDistValue(d1) - strikeprice*NormsDistValue(d2));
			}
		}
		else
		{
			d1 = (Math.log(stockprice/strikeprice) + (r+volatility*volatility/2)*t) / (volatility*Math.sqrt(t));
			d2 = d1 -volatility*Math.sqrt(t);
			if (direct!=OPTION_CALL) //认沽期权
			{
				sp = Math.exp(-r*t)*strikeprice*NormsDistValue(-d2) - stockprice*NormsDistValue(-d1);
			}
			else //认购期权
			{
				sp = stockprice*NormsDistValue(d1) - Math.exp(-r*t)*strikeprice*NormsDistValue(d2);
			}
		}

		return sp;
	}


	//计算隐含波动率
	//direct=权证方向，0-CALL,1-PUT,type=权证类型0-股票期权，1-股指期权，2-期货期权
	//stockprice=当前标的物价格，strikeprice=权证行权价格，
	//volatility=标的物历史波动率，r=无风险利率，t=期权期限，以年为单位
	//optionprive=当前期权价格
	//返回值：期权隐含波动率，<0表示计算失败
	public	static double GetImpliedVolatility(int direct,int type, double stockprice, double strikeprice, double volatility, double r, double t, double optionprice)
	{
		double v1=volatility;
		double v2=volatility;
		double vr=volatility;
		double op=GetOptionBSPrice(direct, type, stockprice, strikeprice, volatility, r, t);
		int count = 0;

		//确定一个寻找范围
		if (op>optionprice) //隐含波动率较历史波动率低
		{
			v1 = 0.0001;
		}
		else if (op<optionprice) //隐含波动率较历史波动率高
		{
			do 
			{
				v2 = v2*2+0.0001;
				op=GetOptionBSPrice(direct, type, stockprice, strikeprice, v2, r, t);
				count++;
			} while(op<optionprice && count<=30);
		}
		else
			return volatility;

		double dif = 1.0;
		count = 0;

		while ( dif>0.0001 && count<1000 )
		{
			count++;
			vr = (v1+v2)/2;
			op=GetOptionBSPrice(direct, type, stockprice, strikeprice, vr, r, t);
			dif = Math.abs(op - optionprice);
			if ( op<optionprice )
			{
				v1 = vr;	
			}
			else
			{
				v2 = vr;
			}
		}

		return vr;
	}

	//计算一个欧式期权的Delta值
	//direct=权证方向，0-CALL,1-PUT,type=权证类型0-股票期权，1-股指期权，2-期货期权
	//stockprice=当前标的物价格，strikeprice=权证行权价格，
	//volatility=标的物历史波动率，r=无风险利率，t=期权期限，以年为单位
	//返回值：期权Delta
	public	static double GetOptionDelta(int direct,int type, double stockprice, double strikeprice, double volatility, double r, double t)
	{
		double delta=1.0;
		double d1;

		if (stockprice<=0 || strikeprice<=0 || t<=0 || volatility<=0)
			return 1.0;

		if (type!=OPTION_STOCK) //股指期权和期货期权模型相同
		{
			d1 = (Math.log(stockprice/strikeprice) + volatility*volatility*t/2) / (volatility*Math.sqrt(t));
			if (direct!=OPTION_CALL) //认沽期权
			{
				delta = Math.exp(-r*t)*(NormsDistValue(d1)-1);
			}
			else //认购期权
			{
				delta = Math.exp(-r*t)*NormsDistValue(d1);
			}
		}
		else
		{
			d1 = (Math.log(stockprice/strikeprice) + (r+volatility*volatility/2)*t) / (volatility*Math.sqrt(t));
			if (direct!=OPTION_CALL) //认沽期权
			{
				delta = NormsDistValue(d1)-1;
			}
			else //认购期权
			{
				delta = NormsDistValue(d1);
			}
		}

		return delta;
	}

	//计算一个欧式期权的Theta值，股票期权和股指期权的计算中都不考虑股息影响
	//direct=权证方向，0-CALL,1-PUT,type=权证类型0-股票期权，1-股指期权，2-期货期权
	//stockprice=当前标的物价格，strikeprice=权证行权价格，
	//volatility=标的物历史波动率，r=无风险利率，t=期权期限，以年为单位
	//返回值：Theta值
	public	static double GetOptionTheta(int direct,int type, double stockprice, double strikeprice, double volatility, double r, double t)
	{
		double theta=0.0;
		double d1,d2;

		if (stockprice<=0 || strikeprice<=0 || t<=0 || volatility<=0)
			return 0;

		if (type!=OPTION_STOCK) //股指期权和期货期权模型相同
		{
			d1 = (Math.log(stockprice/strikeprice) + volatility*volatility*t/2) / (volatility*Math.sqrt(t));
			d2 = d1 -volatility*Math.sqrt(t);
			if (direct!=OPTION_CALL) //认沽期权
			{
				theta = (-stockprice*NormsDistDensity(d1)*volatility*Math.exp(-r*t))/2.0/Math.sqrt(t)
						- r*stockprice*NormsDistValue(d1)*Math.exp(-r*t)
						+ r*strikeprice*Math.exp(-r*t)*NormsDistValue(d2);
			}
			else //认购期权
			{
				theta = (-stockprice*NormsDistDensity(d1)*volatility*Math.exp(-r*t))/2.0/Math.sqrt(t)
						+ r*stockprice*NormsDistValue(d1)*Math.exp(-r*t)
						- r*strikeprice*Math.exp(-r*t)*NormsDistValue(d2);
			}
		}
		else
		{
			d1 = (Math.log(stockprice/strikeprice) + (r+volatility*volatility/2)*t) / (volatility*Math.sqrt(t));
			d2 = d1 -volatility*Math.sqrt(t);
			if (direct!=OPTION_CALL) //认沽期权
			{
				theta = (-stockprice*NormsDistDensity(d1)*volatility)/2.0/Math.sqrt(t)
						+ r*strikeprice*Math.exp(-r*t)*NormsDistValue(d2);
			}
			else //认购期权
			{
				theta = (-stockprice*NormsDistDensity(d1)*volatility)/2.0/Math.sqrt(t)
						- r*strikeprice*Math.exp(-r*t)*NormsDistValue(d2);
			}
		}

		return theta;
	}

	//计算一个欧式期权的Gamma值，股票期权和股指期权的计算中都不考虑股息影响
	//direct=权证方向，0-CALL,1-PUT,type=权证类型0-股票期权，1-股指期权，2-期货期权
	//stockprice=当前标的物价格，strikeprice=权证行权价格，
	//volatility=标的物历史波动率，r=无风险利率，t=期权期限，以年为单位
	//返回值：Gamma值
	public	static double GetOptionGamma(int direct,int type, double stockprice, double strikeprice, double volatility, double r, double t)
	{
		double gamma=0.0;
		double d1;

		if (stockprice<=0 || strikeprice<=0 || t<=0 || volatility<=0)
			return 0;

		if (type!=OPTION_STOCK) //股指期权和期货期权模型相同
		{
			d1 = (Math.log(stockprice/strikeprice) + volatility*volatility*t/2) / (volatility*Math.sqrt(t));
			gamma = NormsDistDensity(d1)*Math.exp(-r*t)/stockprice/volatility/Math.sqrt(t);
		}
		else
		{
			d1 = (Math.log(stockprice/strikeprice) + (r+volatility*volatility/2)*t) / (volatility*Math.sqrt(t));
			gamma = NormsDistDensity(d1)/stockprice/volatility/Math.sqrt(t);
		}

		return gamma;
	}

	//计算一个欧式期权的Vega值，股票期权和股指期权的计算中都不考虑股息影响
	//direct=权证方向，0-CALL,1-PUT,type=权证类型0-股票期权，1-股指期权，2-期货期权
	//stockprice=当前标的物价格，strikeprice=权证行权价格，
	//volatility=标的物历史波动率，r=无风险利率，t=期权期限，以年为单位
	//返回值：Vega值
	public	static double GetOptionVega(int direct,int type, double stockprice, double strikeprice, double volatility, double r, double t)
	{
		double vega=0.0;
		double d1;

		if (stockprice<=0 || strikeprice<=0 || t<=0 || volatility<=0)
			return 0;

		if (type!=OPTION_STOCK) //股指期权和期货期权模型相同
		{
			d1 = (Math.log(stockprice/strikeprice) + volatility*volatility*t/2) / (volatility*Math.sqrt(t));
			vega = stockprice * Math.sqrt(t) * NormsDistDensity(d1) * Math.exp(-r*t);
		}
		else
		{
			d1 = (Math.log(stockprice/strikeprice) + (r+volatility*volatility/2)*t) / (volatility*Math.sqrt(t));
			vega = stockprice * Math.sqrt(t) * NormsDistDensity(d1);
		}

		return vega;
	}

	//计算一个欧式期权的Rho值，股票期权和股指期权的计算中都不考虑股息影响
	//direct=权证方向，0-CALL,1-PUT,type=权证类型0-股票期权，1-股指期权，2-期货期权
	//stockprice=当前标的物价格，strikeprice=权证行权价格，
	//volatility=标的物历史波动率，r=无风险利率，t=期权期限，以年为单位
	//返回值：Rho值
	public	static double GetOptionRho(int direct,int type, double stockprice, double strikeprice, double volatility, double r, double t)
	{
		double rho=0.0;
		double d1,d2;

		if (stockprice<=0 || strikeprice<=0 || t<=0 || volatility<=0)
			return 0;

		if (type!=OPTION_STOCK) //股指期权和期货期权模型相同
		{
			d1 = (Math.log(stockprice/strikeprice) + volatility*volatility*t/2) / (volatility*Math.sqrt(t));
			d2 = d1 -volatility*Math.sqrt(t);
		}
		else
		{
			d1 = (Math.log(stockprice/strikeprice) + (r+volatility*volatility/2)*t) / (volatility*Math.sqrt(t));
			d2 = d1 -volatility*Math.sqrt(t);
		}
		if (direct!=OPTION_CALL) //认沽期权
			rho	 = -strikeprice * t * Math.exp(-r*t) * NormsDistValue(-d2);
		else //认购期权
			rho	 = strikeprice * t * Math.exp(-r*t) * NormsDistValue(d2);

		return rho;
	}

	//计算在特定波动率下一个从一个价格变化到另外一个价格之外的概率
	//direct=权证方向，0-CALL,1-PUT,type=权证类型0-股票期权，1-股指期权，2-期货期权
	//price_from=标的初始价格,price_to=标的最终价格,strikeprice权证行权价格
	//volatility=价格波动率，t=过程所经历的时间,按年计算
	//返回值：这种情况出现的概率
	public	static double GetPOLProbability(int direct,int type, double price_from, double price_to, double strikeprice, double volatility, double r, double t)
	{
		double p=0.0;

		if (price_from<=0 || price_to<=0 || t<=0 || volatility<=0)
			return 0.0;
//		double delta = GetOptionDelta(direct, type, price_from, price_to, volatility, r, t);
		double delta = GetOptionDelta((price_to>=price_from)?OPTION_CALL:OPTION_PUT, type, price_from, price_to, volatility, r, t);
		p = Math.abs(delta);

		return p;
	}


	//计算在特定波动率下一个从一个价格变化到一个价格范围之内的概率
	//type=权证类型0-股票期权，1-股指期权，2-期货期权
	//price_from=标的初始价格,price_up=标的最终价格上限,标的最终价格下限
	//volatility=价格波动率，t=过程所经历的时间,按年计算
	//返回值：这种情况出现的概率
	public	static double GetPIRProbability(int type, double price_from, double price_up, double price_down, double volatility, double r, double t)
	{
		double p=0.0;

		if (price_from<=0 || price_up<=0 || price_down<=0 || t<=0 || volatility<=0)
			return 0.0;

		double p1 = GetOptionDelta(OPTION_CALL, type, price_from, price_down, volatility, r, t);
		double p2 = GetOptionDelta(OPTION_PUT, type, price_from, price_up, volatility, r, t);
		p = Math.abs(p1) + Math.abs(p2) - 1.0;
		return Math.max(p , 0.0);
	}

	//计算在特定波动率下出现指定概率的价格
	//probability=概率
	//type=权证类型0-股票期权，1-股指期权，2-期货期权
	//price_from=标的初始价格,price_down=标的最终价格下限,price_up=标的最终价格上限
	//volatility=价格波动率，t=过程所经历的时间,按年计算
	//返回值：出现这种概率的价格
	public	static double GetPriceFromProbability(double probability,int type, double price_from,double price_down, double price_up, double volatility, double r, double t)
	{
	    double dPrice = 0;
	    
	    double tempProbability = 0;
	    double middle = (price_down + price_up)/2;
	    if (middle >= price_up || middle <= price_down) {
	        return middle;
	    }
	    tempProbability = GetPIRProbability(type, price_from, middle, price_down, volatility, r, t);
	    if (tempProbability > probability) {
	        return GetPriceFromProbability(probability, type, price_from, price_down, middle, volatility,r, t);
	    }
	    else if (tempProbability < probability)
	    {
	        return GetPriceFromProbability(probability, type, price_from, middle, price_up, volatility,r, t);
	    }
	    else
	    {
	        dPrice = middle;
	    }
	    
	    return dPrice;
	}

//	//计算个股期权的估算损益比
//	////direct=权证方向，0-认购,1-认沽
//	//type=权证类型0-股票期权，1-股指期权，2-期货期权
//	//optionPrice=个股期权当前价格,stockPrice=标的当前价格,optionExecutePrice=合约行权价格
//	//volatility=价格波动率，t=过程所经历的时间,按年计算
//	//返回值：个股期权的估算损益比
//	public	static double GetOptionGSSYB(int direct,int type, double optionPrice,double stockPrice, double optionExecutePrice, double volatility, double r, double t)
//	{
//	    double dGSSYB = 0;
//	    //以stockPrice为中间点，高低各取40个点，每个点之间的涨跌幅度为2%
//	    float price[PROFITANALYSE_TOTAL_COUNT] = {0};    //价格
//	    float rate[PROFITANALYSE_TOTAL_COUNT] = {0};     //发生概率
//	    //float percentPrice[PROFITANALYSE_TOTAL_COUNT] = {0};  //相对于中间点的百分比
//	    
//	    int nMiddleIndex = PROFITANALYSE_TOTAL_COUNT/2;
//	    price[nMiddleIndex] = stockPrice;
//	    //percentPrice[nMiddleIndex] = 1;
//	    
//	    for (int i = 1; i <= nMiddleIndex; i++) {
//	        
//	        price[nMiddleIndex-i] = (1-PROFITANALYSE_PRICE_RATE*i)*stockPrice;
//	        price[nMiddleIndex+i] = (1.0/(1-PROFITANALYSE_PRICE_RATE*i))*stockPrice;
//	        
//	        //percentPrice[nMiddleIndex-i] = (1-PROFITANALYSE_PRICE_RATE*i);
//	        //percentPrice[nMiddleIndex+i] = (1.0/(1-PROFITANALYSE_PRICE_RATE*i));
//	    }
//	    
//	    double totalLoss = 0; //亏
//	    int nLossCount = 0;
//	    double totalProfit = 0;//盈
//	    int nProfitCount = 0;
//	    for (int i = 1; i < PROFITANALYSE_TOTAL_COUNT-2; i++) {
//	        
//	        rate[i] = GetPIRProbability(0, stockPrice, price[i+1], price[i-1], volatility, r, t);
//	        
//	        float fTempProfit = 0;
//	        //0->看涨(认购)  1->看跌(认沽)
//	        if (direct == 0) {
//	            fTempProfit = (price[i] - (optionExecutePrice+optionPrice));
//	        }
//	        else
//	        {
//	            fTempProfit = ((optionExecutePrice-optionPrice) - price[i]);
//	        }
//	        if (fTempProfit < 0 && ABS(fTempProfit) > optionPrice) {
//	            //最大亏损为保证金
//	            fTempProfit = -1*optionPrice;
//	        }
//	        if (fTempProfit > 0) {
//	            totalProfit += fTempProfit*rate[i];
//	            nProfitCount++;
//	        }
//	        else if (fTempProfit < 0)
//	        {
//	            totalLoss += fTempProfit*rate[i];
//	            nLossCount++;
//	        }
//	        //DebugLog(@"ProfitRecord[%d] percentPrice=%f%% price=%f rate=%f profit=%f",i,percentPrice[i]*100,price[i],rate[i],fTempProfit);
//	    }
//	    
//	    if (nLossCount > 0) {
//	        totalLoss = totalLoss/nLossCount;
//	    }
//	    if (nProfitCount > 0) {
//	        totalProfit = totalProfit/nProfitCount;
//	    }
//	    
//	    //估算损益比
//	    if (totalProfit != 0) {
//	        dGSSYB = ABS(totalLoss)/totalProfit;
//	    }
//	    
//	    return dGSSYB;
//	}
}
