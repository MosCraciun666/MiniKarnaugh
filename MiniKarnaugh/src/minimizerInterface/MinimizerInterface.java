package minimizerInterface;

import atomi.ConsecutiveOperatorsException;
import exceptii.*;
import minimizer.CelulaKarnaugh;
import minimizer.Minimizer;


import java.util.Collections;
import java.util.Vector;

import stiva.StackProcessor;
import atomi.Atom;
import atomi.Tip;
import exceptii.ExceededLengthException;
import exceptii.InvalidCharacterException;
import exceptii.NoExpressionException;


public class MinimizerInterface {
	
	private static MinimizerInterface instance;
	private static String resultAsString;
	private static int[] resultAsTruthTable,resultAsKarnaughMap;
        public static Vector<Atom> simboluri;
	private MinimizerInterface()
	{		
	}
	public static MinimizerInterface getInstance()
	{
		if(instance==null) instance=new MinimizerInterface();
		return instance;
	}
        public static String getResultAsString(){return resultAsString;}
        public static int[] getResultAsTruthTable(){return resultAsTruthTable;}
        public static int[] getResultAsKarnaughMap(){return resultAsKarnaughMap;}

	private static String expressionAsString(Vector<Atom> v)
	{
		StringBuffer result=new StringBuffer();
		int i;
		for(i=0;i<v.size();i++)
                {
                    if(v.get(i).getName().equals("+")==true)
                        result.append(" ");
                    result.append(v.get(i).getName());
                    if(v.get(i).getName().equals("+")==true)
                        result.append(" ");
                }
		return result.toString();
	}
        public static int getNumberOfSymbols(){return simboluri.size();}
        public static Vector<Atom> getSymbols() {return simboluri;}
	
	public static void minimizeFromExpression(String expression) throws InvalidCharacterException, ExceededLengthException, NoExpressionException, IllegalStartException, ConsecutiveOperatorsException, IllegalEndException, InvalidParanthesesException, TooManyVariablesException
	{
		
		/***SCENARIUL URMATOR SPER(M)AM SA FIE CAT MAI REALIST, BEA-MI-AI PULA*/
		/**ASA CA NU IL STERGE, FUTE-TE-AS IN GAT*/
		
		//luam atomii dintr-o expresie 
		Vector<Atom> initialAtoms,result;
		int[] truthTable=null;
		int[] karnaugh;
		Atom.parse(expression);
                long t1=System.currentTimeMillis(),t2;
                long nt1=System.nanoTime(),nt2;

                Minimizer.setFromMap(false);
	
		initialAtoms=Atom.getAtomiPrelucrati();
		if(expression.length()==0) throw new exceptii.NoExpressionException();
                

		//prelucram cu stiva, sa evaluam expresia si sa scoatem tabelul de adevar
		StackProcessor.parse(initialAtoms);
		StackProcessor.printResult();
		truthTable=StackProcessor.truthTable();
		resultAsTruthTable=truthTable;
		karnaugh=StackProcessor.karnaughMap(truthTable);
		resultAsKarnaughMap=karnaugh;
                System.out.println("+FIRIA DREQ DA CACAT!!!!!!!!!!!!!!!!!!!!!!!!!");
                int i;
                for(i=0;i<karnaugh.length;i++)
                    System.out.print(karnaugh[i]+" - ");
                System.out.println();
                for(i=0;i<karnaugh.length;i++)
                    System.out.print(truthTable[i]+" = ");
                System.out.println("-FIRIA DREQ DA CACAT!!!!!!!!!!!!!!!!!!!!!!!!!");
		
		
	    //obtinem simbolurile distincte:
		simboluri=StackProcessor.getSymbols();
                if(simboluri.size()>8) throw new TooManyVariablesException(""+simboluri.size());
		//si le setam celulei (apoi minimizerului?)
		Collections.reverse(simboluri);/**AI MARE GRIJA SA NU INVERSEZI DE 2X */
		CelulaKarnaugh.setVariables(simboluri);	
		
		//minimizam
		Minimizer miniPula=Minimizer.getInstance();
                miniPula.initializeaza(karnaugh);//OLD BUT GOOD: 
                //miniPula.initializeaza(getResultAsTruthTable());//NEW AND PROBABLY SHIT!
	    
                try
                {
                    result=miniPula.minimizeaza();
                    resultAsString=expressionAsString(result);
		} 
                catch (NeinitializatException e)
                {
                    e.printStackTrace();
		}
                catch (DifferentSizesException e)
                {
                    e.printStackTrace();
                }
	    
            Atom.expresieCorecta();
            t2=System.currentTimeMillis();
            nt2=System.nanoTime();
            System.out.println("Timp de executie: " +((t2-t1)/1000));
            System.out.println("Nanotime: " +((nt2-nt1)/1000000000));
	}
	
	public static void minimizeFromKarnaughMap(int[] kmap)
	{
		int i,numberOfSymbols=(int)(Math.log((double)kmap.length)/Math.log(2.0));/*schimbarea bazei logaritmului din 2 in e*/;
		Vector<Atom> simboluri,result;
		String[] varNames={"A","B","C","D","E","F","G","H"};
		simboluri=new Vector<Atom>();
                long t1=System.currentTimeMillis(),t2;
                long nt1=System.nanoTime(),nt2;

                Minimizer.setFromMap(true);

		System.out.println("Nos="+numberOfSymbols);
		
		for(i=0;i<numberOfSymbols;i++)
			simboluri.add(new Atom(varNames[i],Tip.VARIABILA));
		StackProcessor.setSymbols(simboluri);//inutil?

                System.out.println("+FIRIA DREQ DA CACAT!!!!!!!!!!!!!!!!!!!!!!!!!");
                for(i=0;i<kmap.length;i++)
                    System.out.print(kmap[i]+" - ");
                System.out.println("-FIRIA DREQ DA CACAT!!!!!!!!!!!!!!!!!!!!!!!!!");

		//si le setam celulei (apoi minimizerului?)
		Collections.reverse(simboluri);/**AI MARE GRIJA SA NU INVERSEZI DE 2X */
		CelulaKarnaugh.setVariables(simboluri);	
		
		//minimizam pula
		Minimizer miniPula=Minimizer.getInstance();
                miniPula.initializeaza(kmap);
	    
                try
                {
                    result=miniPula.minimizeaza();
                    System.out.println("Am asa:");
                    resultAsString=expressionAsString(result);
                    System.out.println(resultAsString);
		} 
                catch (NeinitializatException e)
                {
                    e.printStackTrace();
		}
                catch (DifferentSizesException e)
                {
                    e.printStackTrace();
                }
                catch (TooManyVariablesException e)
                {
                    e.printStackTrace();
		}
            t2=System.currentTimeMillis();
            nt2=System.nanoTime();
            System.out.println("Timp de executie: " +((t2-t1)/1000));
            System.out.println("Nanotime: "+((nt2-nt1)/1000000000));
	}
	
	public static void main(String[] args) throws InvalidCharacterException, ExceededLengthException
	{
		int[] testMap=
		{
				0,1,1,1,
				1,1,1,1,
				1,1,0,1,
				1,0,0,1
		};
		//minimizeFromExpression("a+b+c+d+e");
		minimizeFromKarnaughMap(testMap);
	}

}
