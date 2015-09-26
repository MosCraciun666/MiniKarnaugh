package atomi;

/*
 * FA-L CASE INSENSTITIVE
 * SI SA DETECTEZE ERORI DE TIPUL  A*+B
 * */

import java.util.Vector;

import exceptii.ExceededLengthException;
import exceptii.IllegalEndException;
import exceptii.IllegalStartException;
import exceptii.InvalidCharacterException;
import exceptii.InvalidParanthesesException;
import exceptii.NoExpressionException;

public class Atom 
{
private String nume;
private Tip tip;
private int precedenta;
private static Vector<Atom> atomiPrelucrati;//astia-s atomii obtinuti de la parsarea unui String

public Atom(String nume,Tip tip)
 {
	this.nume=nume;
	this.tip=tip;
	precedenta=-1;//precedenta implicit nedefinita
	if(tip==Tip.OPERATOR)
	{
		if(nume.equals("NOT")) precedenta=1000;//1000
		if(nume.equals("AND")||nume.equals("NAND")) precedenta=999;
		if(nume.equals("XOR")||nume.equals("BIC")) precedenta=998;
		if(nume.equals("OR")||nume.equals("NOR")) precedenta=997;//cred ca astea 2 au prec.=alea 2 de sus		
	}
	else if(tip==Tip.PARANTEZA) precedenta=1;
 }

public int getPrecedence() {return precedenta;}
public String getAlternativeName(){return nume;}
public String getName()
{
    if(nume.equals("OR")) return "+";
    if(nume.equals("NOR")) return "+.";
    if(nume.equals("AND")) return "*";
    if(nume.equals("NOT")) return ".";
    if(nume.equals("XOR")) return "#";
    if(nume.equals("BIC")) return "#.";
    return nume;
}

public String toString()
 {
	return "Nume atom: " +nume +" tip: "+tip+"\n";
 }
public static void reset()
{
	atomiPrelucrati=new Vector<Atom>();//inutil,mananca timp si memorie
	//pentru ca la o parsare noua iar o sa fac atomiPrelucrati=new Vector<Atom>(); 
	//dar daca pun atomiPrelucrati=null, o sa am probleme
}
public static Vector<Atom> getAtomiPrelucrati() {return atomiPrelucrati;}
public static int getNrAtomiPrelucrati(){return atomiPrelucrati.size();}


public static void parse(String sir) throws InvalidCharacterException,ExceededLengthException//rezultatul nu va fi returnat imediat, ci stocat in campul static pt. a putea fi preluat din nou oricand fara o parsare noua 
{
	atomiPrelucrati=new Vector<Atom>();
	int stare=0,caracterCurent=0,ok=0,literaCurenta=0;
	char[] numeVariabila=new char[20];
	char c;
	boolean stareTerminala=false;
	while(caracterCurent<sir.length() || stareTerminala==false)
	{
		stareTerminala=false;
		try
		{
		c=sir.charAt(caracterCurent);
		}
		catch(Exception e)
		{
			c='\0';
		}

	//	if(caracterCurent>=19) throw new ExceededLengthException();
		caracterCurent++;
		ok=0;
		switch(stare)
		{
		case 0://start
			literaCurenta=0;
			if(esteCaracterAlb(c)){ok=1;}//stare= tot 0
			if(esteLitera(c)) {caracterCurent--;ok=1;stare=1;numeVariabila=new char[20000];}
			if(c=='+') {ok=1;stare=3;}
			if(c=='*') {ok=1;stare=4;}
			if(c=='.') {ok=1;stare=5;}
			if(c=='#') {ok=1;stare=6;}
			if(c=='(') {ok=1;stare=18;}
			if(c==')') {ok=1;stare=19;}
			if(ok==0) throw new InvalidCharacterException("Invalid character: "+c);
		break;
		
		case 1://in mijlocul compunerii numelor de variabile
			if(esteAlfaNumeric(c)) 
			{
				numeVariabila[literaCurenta]=c;
				if(literaCurenta>19) throw new ExceededLengthException();
				literaCurenta++;
			}//stare=tot 1
			else
			{
				caracterCurent--;
				stare=2;
			}
		break;
		
		case 2://la sfarsitul numelui unei variabile
			int numarCaractere=caracterCurent;
			for(int i=0;i<caracterCurent;i++)
				if(!esteAlfaNumeric(numeVariabila[i]))
					numarCaractere--;
			String numeFinal=String.copyValueOf(numeVariabila,0,numarCaractere);
			numeFinal=numeFinal.toUpperCase();
			caracterCurent--;			
			if(esteCuvantCheie(numeFinal)) atomiPrelucrati.add(new Atom(numeFinal,Tip.OPERATOR));
			else atomiPrelucrati.add(new Atom(numeFinal,Tip.VARIABILA));
			stareTerminala=true;
			stare=0;
		break;
		
		case 3://SAU
			if(c=='.') stare=8;
			else stare=7;
			caracterCurent--;
		break;
		
		case 4://SI
			if(c=='.') stare=10;
			else stare=9;
			caracterCurent--;
		break;
		
		case 5://NU
			atomiPrelucrati.add(new Atom("NOT",Tip.OPERATOR));
			stareTerminala=true;
			stare=0;
			caracterCurent--;
		break;
		
		case 6://SAU EXCLUSIV	
			if(c=='.') stare=14;
			else stare=13;
			caracterCurent--;
		break;
		
		case 7://SFARSIT SAU
			caracterCurent--;
			atomiPrelucrati.add(new Atom("OR",Tip.OPERATOR));
			stareTerminala=true;
			stare=0;
		break;
		
		case 8://SFARSIT SAU-NU
			atomiPrelucrati.add(new Atom("NOR",Tip.OPERATOR));
			stareTerminala=true;
			stare=0;			
		break;
		
		case 9://SFARSIT SI
			caracterCurent--;
			atomiPrelucrati.add(new Atom("AND",Tip.OPERATOR));
			stareTerminala=true;
			stare=0;			
		break;
		
		case 10://SFARSIT SI-NU
			atomiPrelucrati.add(new Atom("NAND",Tip.OPERATOR));
			stareTerminala=true;
			stare=0;			
		break;
		
		case 13://SFARSIT SAU EXCLUSIV
			caracterCurent--;
			atomiPrelucrati.add(new Atom("XOR",Tip.OPERATOR));
			stareTerminala=true;
			stare=0;			
		break;
		
		case 14://SFARSIT COINCIDENTA
			atomiPrelucrati.add(new Atom("BIC",Tip.OPERATOR));
			stareTerminala=true;
			stare=0;			
		break;
		
		case 18://NU
			atomiPrelucrati.add(new Atom("(",Tip.PARANTEZA));
			stareTerminala=true;
			stare=0;
			caracterCurent--;
		break;
		
		case 19://NU
			atomiPrelucrati.add(new Atom(")",Tip.PARANTEZA));
			stareTerminala=true;
			stare=0;
			caracterCurent--;
		break;
		
		default: System.err.println("Stare necunoscuta: "+stare); break;
		}
	}
	//System.out.println("Sfarsit; atomi prelucrati: "+atomiPrelucrati.size());
	
}

//verifica corectitiudinea expresiei prelucrate anterior
//este void, deci arunca exceptii la erori si nu face nimic atunci cand este corect 
public static void expresieCorecta() throws IllegalStartException,NoExpressionException, ConsecutiveOperatorsException, IllegalEndException, InvalidParanthesesException
{
	int i,nrParantezeDeschise=0,nrParantezeInchise=0;
	if(atomiPrelucrati.size()==0) throw new NoExpressionException();
	Atom atomCurent=atomiPrelucrati.get(0),atomUrmator=null;
	if(atomCurent.nameEquals(")")) throw new IllegalStartException("Cannot start expression with \')\'");
	if(atomCurent.typeEquals(Tip.OPERATOR)) throw new IllegalStartException("Cannot start expression with an operator");
	
	for(i=0;i<atomiPrelucrati.size()-1;i++)
	{
		atomCurent=atomiPrelucrati.get(i);
		atomUrmator=atomiPrelucrati.get(i+1);

                if(atomCurent.nameEquals(")")&&atomUrmator.typeEquals(Tip.VARIABILA)) throw new InvalidParanthesesException();
                if(atomUrmator.nameEquals("(")&&atomCurent.typeEquals(Tip.VARIABILA)) throw new InvalidParanthesesException();

		if(atomUrmator.nameEquals("NOT")&&atomCurent.nameEquals("NOT")) throw new ConsecutiveOperatorsException();
		if(atomUrmator.typeEquals(Tip.OPERATOR) &&atomCurent.nameEquals("NOT")==false && atomCurent.typeEquals(Tip.OPERATOR))
			throw new ConsecutiveOperatorsException();		
	}
	
	atomCurent=atomiPrelucrati.get(atomiPrelucrati.size()-1);//ultimul atom
	if(atomCurent.typeEquals(Tip.OPERATOR)&&atomCurent.nameEquals("NOT")==false)//daca ultimul este operator si nu este NOT->eroare
		throw new IllegalEndException("Can\'t end the exception with operators");
	if(atomCurent.nameEquals("(")) throw new IllegalEndException("Can\'t end exception with \'(\'");
	
	for(i=0;i<atomiPrelucrati.size();i++)
	{
		atomCurent=atomiPrelucrati.get(i);
		if(atomCurent.nameEquals("(")) nrParantezeDeschise++;
		if(atomCurent.nameEquals(")")) nrParantezeInchise++;
                if(nrParantezeInchise>nrParantezeDeschise) throw new InvalidParanthesesException();
	}
	if(nrParantezeDeschise!=nrParantezeInchise) throw new InvalidParanthesesException();
}

/*
private static boolean esteCifraBooleana(char c) NU!!!!
{
	return c=='0'||c=='1';
}*/

private static boolean esteCuvantCheie(String s)
{
	if(s.equals("OR")) return true;
	if(s.equals("NOR")) return true;
	if(s.equals("AND")) return true;
	if(s.equals("NAND")) return true;
	if(s.equals("XOR")) return true;
	if(s.equals("BIC")) return true;
	if(s.equals("NOT")) return true;	
	return false;
}

private static boolean esteLitera(char c)
 {
	return ('A'<=c && c<='Z') || ('a'<=c && c<='z'); 
 }

private static boolean esteCifra(char c)
{
	return '0'<=c && c<='9';
}
private static boolean esteAlfaNumeric(char c) 
{
	return esteCifra(c) || esteLitera(c);
}
private static boolean esteCaracterAlb(char c)
{
	return c==' '||c=='\n'||c=='\t';
}

//functie auxiliara pentru testare
public static void printResult()
{
	int i;
	System.out.println("Atomi prelucrati:" +atomiPrelucrati.size());
	for(i=0;i<atomiPrelucrati.size();i++)
		System.out.println(atomiPrelucrati.get(i).toString());
}
//functii auxiliare pentru testare (in assert)
public boolean typeEquals(Tip tip) {return this.tip==tip;}
public boolean nameEquals(String name) {return name.equals(nume);}
}
