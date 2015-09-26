package stiva;

import exceptii.TooManyVariablesException;
import java.util.Vector;

import exceptii.NoExpressionException;
import exceptii.StackEmptyException;

import atomi.*;



public class StackProcessor 
{
private static StackProcessor instance=null;
private static Vector<Atom> postfixAtoms;
private static boolean receivedExpression;
private static int numarVariabileDistincte;
private static Vector<Atom> simboluri;

private StackProcessor()
{
	receivedExpression=false;
	postfixAtoms=new Vector<Atom>();//inutil pentru ca se va instantia din nou la o parsare
									//dar ne asiguram
}

public static StackProcessor getInstance()
{
	if(instance==null) instance=new StackProcessor();
	return instance; 
}

public static int getNumberOfDistinctVariables(){return numarVariabileDistincte;}

public static Vector<Atom> getPostfFixAtoms() throws NoExpressionException
{
	if(!receivedExpression) throw new NoExpressionException();
	return postfixAtoms;
}

public static Vector<Atom> getSymbols() {return simboluri;}
public static void setSymbols(Vector<Atom> s) 
{
	simboluri=s;
	numarVariabileDistincte=simboluri.size();
}
//operatie pe stiva
private static void transfer(Vector<Atom> stiva,Vector<Atom> coada)
{
	coada.add(stiva.get(stiva.size()-1));
	stiva.remove(stiva.size()-1);
}

private static void push(Vector<Atom> v,Atom a)
{
	v.add(v.size(),a);
}

private static void pushInt(Vector<Integer> v,Integer a)
{
	v.add(v.size(),a);
}

private static Atom iaVarf(Vector<Atom> v)  throws StackEmptyException
{
	if(v.size()<=0) throw new StackEmptyException();
	return v.get(v.size()-1);
	}

private static void pop(Vector<Atom> v) {v.remove(v.size()-1);}

private static Integer iaVarfInt(Vector<Integer> v) throws StackEmptyException
{
	if(v.size()<=0) throw new StackEmptyException();
	return v.get(v.size()-1);
}

private static void popInt(Vector<Integer> v) {v.remove(v.size()-1);}

public static void parse(Vector<Atom> atoms) throws TooManyVariablesException//transforma in postfix
{
	Vector<Atom> stivaOperatori=new Vector<Atom>();//stiva operatorilor pentru care nu s-au gasit inca ambii operanzi
	Vector<Atom> coadaPostfix=new Vector<Atom>();//coada expresiei postfix
	Atom atomCurent=null;
	int index=0,nrVar=0;
	receivedExpression=true;
	
	while(index<atoms.size())
	{
		atomCurent=atoms.get(index);
		if(atomCurent.typeEquals(Tip.VARIABILA))
                {
			push(coadaPostfix,atomCurent);
                        nrVar++;
                        if(nrVar>8) throw new exceptii.TooManyVariablesException("");
                }
		else
		{
			if(stivaOperatori.size()==0) 	
				{
				push(stivaOperatori,atomCurent);
				}
			else
				{
				if(atomCurent.nameEquals("("))
					{
				 	push(stivaOperatori,atomCurent);
					}
				else if(atomCurent.nameEquals(")"))
					{
						try
						{
						Atom varf=iaVarf(stivaOperatori);
						while(varf.nameEquals("(")==false)//ia toti operatorii din stiva pana am ajuns la (
						 {
							transfer(stivaOperatori,coadaPostfix);						
							varf=iaVarf(stivaOperatori);
						 }
						pop(stivaOperatori);//scoate "("					
						}
						catch(StackEmptyException e) {e.printStackTrace();}
					}
				else
					{		
					try
					{
						Atom varf=iaVarf(stivaOperatori);
						while(atomCurent.getPrecedence()<=varf.getPrecedence() && stivaOperatori.size()!=0)
							{
								transfer(stivaOperatori,coadaPostfix);
								if(stivaOperatori.size()<=0) break;
								varf=iaVarf(stivaOperatori);
							}
						push(stivaOperatori,atomCurent);
						//System.out.println()
					}
					catch(StackEmptyException e) {e.printStackTrace();}
				}
			}	
		}		
		index++;
	}	
	while(stivaOperatori.size()!=0)
		transfer(stivaOperatori,coadaPostfix);
	postfixAtoms=coadaPostfix;
}


private static boolean maiExista(Vector<Atom> v,Atom a)
{
	int i;
	for(i=0;i<v.size();i++)
		if(v.get(i).toString().equals(a.toString()))
			return true;
	return false;
}


private static int alCateleaEste(Vector<Atom> v,Atom a)
{
	int i;
	for(i=0;i<v.size();i++)
		if(v.get(i).toString().equals(a.toString()))
			return i;
	return -1;
}


//functii pentru evaluarea expresiilor
private static int and(int a,int b)
{
	if(a==1 && b==1) return 1;
	return 0;
}
private static int nand(int a,int b)
{
	if(a==0 && b==0) return 1;
	return 0;
}
private static int or(int a,int b)
{
	if(a==0 && b==0) return 0;
	return 1;
}
private static int nor(int a,int b)
{
	if(a==0 && b==0) return 1;
	return 0;
}
private static int xor(int a,int b)
{
	if(a!=b) return 1;
	return 0;
}
private static int bic(int a,int b)
{
	if(a==b) return 1;
	return 0;
}






//evalueaza expresia postfix pentru toate combinatiile posibile 
public static int[] truthTable()
{
	int[] result=null;
	int[] input;
	int i,numarVariabile=0,j,numarIntrari;
	simboluri=new Vector<Atom>();
	Atom atomCurent;
	for(i=0;i<postfixAtoms.size();i++)
	{
		atomCurent=postfixAtoms.get(i);
		if(atomCurent.typeEquals(Tip.VARIABILA))
		{
                    if(maiExista(simboluri,atomCurent)==false)
                    {
                        push(simboluri,atomCurent);
                        numarVariabile++;
                    }
		}
	}
	System.out.println("Variabilele distincte sunt:");
	numarVariabileDistincte=numarVariabile;
	for(i=0;i<numarVariabile;i++)
		System.out.println(simboluri.get(i));
	
	
	Vector<Integer> stiva=new Vector<Integer>();	
	
	numarIntrari=(int)Math.pow(2,numarVariabile);
	input=new int[numarVariabile];
	result=new int[numarIntrari];
	for(i=0;i<numarIntrari;i++)
	{
		int auxiliar=i,lsb;
		System.out.println("\n\n");
		for(j=0;j<numarVariabile;j++)
		{
			lsb=auxiliar&1;
			auxiliar=auxiliar>>1;
			input[numarVariabile-j-1]=lsb;
			//System.out.println("input["+j+"]="+input[j]);
			//System.out.println("input["+(numarVariabile-j-1)+"]="+input[numarVariabile-j-1]);
		}
		
		for(j=0;j<numarVariabile;j++)
			System.out.println("input["+j+"]="+input[j]);
		
		//aici vine treaba cu stiva
		for(j=0;j<postfixAtoms.size();j++)
		{
			int pozitiaAtomului=0;
			atomCurent=postfixAtoms.get(j);
			if(atomCurent.typeEquals(Tip.VARIABILA))
			{
				pozitiaAtomului=alCateleaEste(simboluri,atomCurent);
				pushInt(stiva,input[pozitiaAtomului]);
				System.out.println("Variabila; pun "+input[pozitiaAtomului]);
			}
			else//parantezele sunt eliminate, doar operatorii au ramas
			{
				int varf1,valoareNoua=0,varf2;;
				try
				{
					varf1=iaVarfInt(stiva);
					popInt(stiva);
					System.out.println("Vf1="+varf1);
					if(atomCurent.nameEquals("NOT")) 
					{
						if(varf1==0) valoareNoua=1;
						else valoareNoua=0;
					}
					else//opertaor binar, mai trebuie un operand
					{
						try
						{
							varf2=iaVarfInt(stiva);
							popInt(stiva);
							System.out.println("Vf2="+varf2);
							if(atomCurent.nameEquals("AND")) valoareNoua=and(varf1,varf2);
							if(atomCurent.nameEquals("NAND")) valoareNoua=nand(varf1,varf2);
							if(atomCurent.nameEquals("OR")) valoareNoua=or(varf1,varf2);
							if(atomCurent.nameEquals("NOR")) valoareNoua=nor(varf1,varf2);
							if(atomCurent.nameEquals("XOR")) valoareNoua=xor(varf1,varf2);
							if(atomCurent.nameEquals("BIC")) valoareNoua=bic(varf1,varf2);
						}
						catch(StackEmptyException e)//not really my business
						{
							System.out.println("WTF?");
							e.printStackTrace();
						}
					
					}
					pushInt(stiva,valoareNoua);
				}
				catch(StackEmptyException e)
				{
					e.printStackTrace();					
				}
				
				System.out.println("V noua="+valoareNoua);
				
			}
		}
		//aici se termina jmenu' cu stiva

		System.out.println("rezultat" +stiva.get(0));
		result[i]=stiva.get(0);
		stiva.remove(0);
	}

	return result;
}

private static int log2(int x)
{
	int i;
	for(i=0;i<x;i++)
		if(Math.pow(2, i)==x)
			return i;
	return -1;
}



public static int karnaughOrder(int y,int x,int numberOfVariables)
{
    int xh,xl,yh,yl;
    int pondereYh,pondereYl,pondereXh;
    int[] smallOrder={0,1,3,2, 4,5,7,6, 12,13,15,14, 8,9,11,10};
    xl=smallOrder[x%4];
    yl=smallOrder[y%4];
    xh=(x&0xC)>>2;
    yh=(y&0xC)>>2;
    if(numberOfVariables<=6) pondereYh=32;
    else pondereYh=64;
    pondereYl=4;
    if(numberOfVariables<=2) pondereYl=2;
    if(numberOfVariables==1) pondereYl=1;

    pondereXh=16;
    //if(numberOfVariables<=4) pondereXh=0;
    System.out.println("xl="+xl +" yl="+yl +" pYL="+pondereYl+" YH="+yh+" pYH="+pondereYh +" xh="+xh +" result="+(xl+yl*pondereYl+xh*pondereXh+yh*pondereYh));
    return xl+yl*pondereYl+xh*pondereXh+yh*pondereYh;
}

public static int karnaughOrder(int i,int tableLength)
{
	int x,y;
	x=i%xSizes[log2(tableLength)];
	y=i/xSizes[log2(tableLength)];
        System.out.println("xsizes="+xSizes[log2(tableLength)] +"tablelength"+tableLength);
	System.out.print("I="+i+" x="+x +"y="+y +"log2="+log2(tableLength));
        System.out.println("return "+karnaughOrder(y,x,log2(tableLength)));
	return karnaughOrder(y,x,log2(tableLength));
}
//provizoriu(VECHI)
/*
public static int[] karnaughMap(int[] table)
{
	int[] k=new int[table.length];
	for(int i=0;i<table.length;i++)
		k[i]=table[kOrder[i]];
	return k;
}*/

private static final int[] xSizes={1,2,4,4,8,8,16,16};
///private static final int[] xSizes={1,2,2,4,4,8,8,16,16};
//private static final int[] ySizes={2,2,4,4,8,8,16,16};//not really used
//actual(NETESTAT)
public static int[] karnaughMap(int[] table)
{
    int[] pulaMea={0,1,3,2, 4,5,7,6, 12,13,15,14, 8,9,11,10};
	int[] k=new int[table.length];
	int x,y;
	System.out.println("Am atatea jmenuri:"+table.length);
        if(table.length==4) 
        {
            k[0]=table[0];
            k[1]=table[1];
            k[2]=table[2];
            k[3]=table[3];
        }
        else if (table.length==8)
        {
            System.out.println("Bai, imi mananci pula cu sange");
            k[0]=table[0];
            k[1]=table[1];
            k[2]=table[3];
            k[3]=table[2];
            k[4]=table[4];
            k[5]=table[5];
            k[6]=table[7];
            k[7]=table[6];

        }
        else if(table.length==16)
        {
            int cacatLaGaoaza=0;
            System.out.println("Am "+table.length+" coaie");
            for(int i=0;i<table.length;i++)
            {
                if(i%4==0) System.out.println();
                System.out.print(table[i]+" ");
            }
            for(int i=0;i<table.length;i++)
            {

		/*x=i%xSizes[log2(table.length)-1];
		y=i/xSizes[log2(table.length)-1];
		//k[i]=table[kOrder[i]];
		k[i]=table[karnaughOrder(y,x,log2(table.length))];*/

                System.out.print(i+". Sugi pula la morti de "+(pulaMea[i%16]+(i/16*16)) +" ori pe zi ");
                k[i]=table[pulaMea[i%16]+(i/16*16)];
                System.out.println(k[i]);
            }
        }
        else
        {
            for(int i=0;i<table.length;i++)
            {
               /* x=i%xSizes[log2(table.length)-1];
		y=i/xSizes[log2(table.length)-1];
		//k[i]=table[kOrder[i]];
		k[i]=table[karnaughOrder(y,x,log2(table.length))];*/
                k[i]=table[i];
            }

        }
	//System.exit(0);	
	return k;
}

private static final int[] kOrder=
{
	0,1,3,2,
	4,5,7,6,
	12,13,15,14,
	8,9,11,10
};

private static boolean isPowerOf2(int x)//2^0==1 consideram ca si asta e putere a lui 2
{
	int i,numberOfOnes=0;
	if(x==0) return false;
	for(i=0;i<32/*presupun ca zsizeof(int) nu se modifica*/;i++)
	{
		if((x&1)==1) numberOfOnes++;
		if(numberOfOnes>1) return false;
		x=x>>1;
	}
	if(numberOfOnes!=1) return false;
	return true;
}






public static void printResult()
{
	int i;
	for(i=0;i<postfixAtoms.size();i++)
		System.out.println(postfixAtoms.get(i).toString());
}




}
