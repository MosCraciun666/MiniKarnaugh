package minimizer;


import java.util.ArrayList;
import java.util.Vector;
import atomi.Atom;
import atomi.Tip;
import exceptii.*;
import stiva.StackProcessor;


public class Minimizer 
{
private ArrayList<CelulaKarnaugh> blocuri;
private boolean initializat;
private int nrEl;

private int tabel[];
private int dontCares[];
private static Minimizer instance;

public static Minimizer getInstance()
{
	if(instance==null) instance=new Minimizer();
	return instance;
}
private static boolean fromMap;
public static void setFromMap(boolean fm){fromMap=fm;}

private Minimizer()
{
	blocuri=new ArrayList<CelulaKarnaugh>();
        //System.out.println("CONSTRUIESC MINIMIZERUL!" +blocuri.size());
	initializat=false;
}


private int nuMaFreca(int x)
{
    int[] pulaCalului={0,1,3,2, 4,5,7,6, 12,13,15,14, 8,9,11,10};
    if(x<16) return pulaCalului[x%16];
    return x;
   // return x%16+(x/16)*16;//sa nu crezi ca x/16*16==x
    
}

public void initializeaza(int[] tabel)
{
	int i;
	nrEl=(int)(Math.log((double)tabel.length)/Math.log(2.0));/*schimbarea bazei logaritmului din 2 in e*/
	
	System.out.println("dim. tabel="+tabel.length);
	System.out.println("nr el="+nrEl);
	initializat=true;
	this.tabel=tabel;//.clone();
        if(tabel.length==4 && fromMap==false)//cazul cu 2 variabile nu pusca, face 0 1 3 2
        {
            int aux=this.tabel[2];
            this.tabel[2]=this.tabel[3];
            this.tabel[3]=aux;
        }
        blocuri.clear();


	for(i=0;i<tabel.length;i++)
        {
            if(i!=0 && i%4==0) System.out.println();
            System.out.print("("+tabel[i]+","+i+") ");
        }
        System.out.println();

        int numberOfDontCares = 0;
	for(i=0;i<tabel.length;i++)
	{
		if(tabel[i]==1)
		{
			CelulaKarnaugh ck=new CelulaKarnaugh(1,nuMaFreca(i)/*StackProcessor.karnaughOrder(i,tabel.length)*/);//era getAddress care returna din tabel
			blocuri.add(ck);
		}
                else if(tabel[i]==2)
                {
                    numberOfDontCares++;//just count,do not assign yet
                }
	}
        dontCares=new int[numberOfDontCares];
        
        //add don't cares
        //nu repeta aia cu cu inversarea din tabel!
        numberOfDontCares=0;
        for(i=0;i<tabel.length;i++)
            if(tabel[i]==2)
                dontCares[numberOfDontCares++]=nuMaFreca(i);
        //+temp
        for(i=0;i<numberOfDontCares;i++)
            System.out.println(i+" don\'t give a shit" +dontCares[i]);
        //-temp
}


private void removeDuplicates(ArrayList<CelulaKarnaugh> l)
{
	int i,j;
        System.out.println("Removing duplicates:");
        int removed=0;
	for(i=0;i<l.size();i++)
		for(j=i+1;j<l.size();j++)
                {
			if(
			CelulaKarnaugh.exactSameElements(l.get(i),l.get(j)) &&
			l.get(i).getNumberOfElementsForReal()>1 &&
			l.get(j).getNumberOfElementsForReal()>1)
			{
				l.remove(j);
                                removed++;
                                System.out.println("removing i="+i+" j="+j+" / "+l.size());
			}
                }
        System.out.println("REMOVED "+removed);
}

private static boolean containsDuplicate(CelulaKarnaugh ck1,ArrayList<CelulaKarnaugh> l)
{
	int i;
	for(i=0;i<l.size();i++)
		if(CelulaKarnaugh.exactSameElements(ck1, l.get(i)))
                    return true;
	return false;
}


//assumes cell groups are ordered from smallest to biggest
//de ce pula mea scrii pe engleza?
private static boolean removeIfInLargerCells(int what,CelulaKarnaugh ck1,ArrayList<CelulaKarnaugh> l)
{
	int i;
	for(i=0;i<l.size();i++)
		if(ck1.getNumberOfElementsForReal()<l.get(i).getNumberOfElementsForReal() &&
				CelulaKarnaugh.exactSameElementsIgnoreLength(ck1,l.get(i)))
		{
                       l.remove(what);
		    return true;
		}
	return false;
}


private boolean specialCase(int _case)
{	
	for(int i=0;i<tabel.length;i++)
		if(tabel[i]!=_case && tabel[i]!=2)
			return false;
	return true;
}

public void reset()
{
 blocuri.clear();
}



private void removeUselessCrosses()
{
    /*
     * this is supposed to remove errors in cases like:
     * 0,0,1,1,
     * 0,1,1,0
     * da de ce pula mea scrii iara pe engleza?
     */
    int i,j,limita,k,l,limita2;
    int valoareCurenta,val2;
    boolean found=false;
    CelulaKarnaugh blocCurent;
    System.out.println("Atentie, SA IL FACI DUPA REMOVEDUPLICATES");
    for(i=0;i<blocuri.size();i++)
    {
        found=false;
        blocCurent=blocuri.get(i);
        limita=blocCurent.getNumberOfElements();

        for(j=0;j<limita;j++)//search through all elements in the block. futu-ti engleza ma-tii sa-ti fut io astazi si maine
        {
            if(blocCurent.getNumberOfElements()!=1)
                valoareCurenta=blocCurent.getElement(j).getAddress();
            else valoareCurenta=blocCurent.getAddress();
         
            found=false;
            for(k=0;k<blocuri.size();k++)//search for current value in other blocks. iara scrii pe engleza?
            {
                if(k!=i)
                {
                    limita2=blocuri.get(k).getNumberOfElements();

                    for(l=0;l<limita2;l++)
                    {
                        if(limita2==1) val2=blocuri.get(k).getAddress();
                        else val2=blocuri.get(k).getElement(l).getAddress();

                        if(valoareCurenta==val2)
                        {
                            found=true;//found: skip search
                            break;
                        }
                    }
                }
                if(found) break;//found: skip search
            }//end for k
            if(!found) break;//not found: this does not need to be removed
        }//end for j
        
        if(found) 
        {
            blocuri.remove(i);
        }
    }//end big for (i)
}

private void resetBlocksBeforeFor()
{
 int i;
 blocuri.clear();
 for(i=0;i<tabel.length;i++)
     if(tabel[i]==1)
	{
		CelulaKarnaugh ck=new CelulaKarnaugh(1,nuMaFreca(i)/*StackProcessor.karnaughOrder(i,tabel.length)*/);//era getAddress care returna din tabel
		blocuri.add(ck);
	}
}
public Vector<Atom> minimizeaza() throws NeinitializatException, TooManyVariablesException, DifferentSizesException
{
	int i,j,size;
        int initialSize,last,totalIteratii=0;
	CelulaKarnaugh ckNoua;
	Vector<Atom> result=new Vector<Atom>();
        ArrayList<CelulaKarnaugh> solutionBlocks=new ArrayList<CelulaKarnaugh>();
	if(!initializat) throw new NeinitializatException();
	System.out.println("Minimiza-mi-ai pula (ca e prea mare si nu-ti incape in gura)");
	int min=20000;
        int numberOfTerms=20000;
	
	if(specialCase(1))
	{
		result.add(new Atom("1",Tip.CONSTANTA));
		return result;
	}
	if(specialCase(0))
	{
		result.add(new Atom("0",Tip.CONSTANTA));
		return result;
	}

        System.out.println("Ba!! INAINTE lista are "+blocuri.size()+" elemente");

        last=0;
        j=0;
        int badShit=0,goodShit=0;
        int dontCareRounds;
        int dc=0;
        dontCareRounds = (int)Math.pow(2, dontCares.length);
        //2^0=1, asa ca nu trebuie sa faci inca un caz pentru 0 'don't cares', ba boule
        System.out.println("I will not give a shit: "+dontCareRounds);


        for(dc=0;dc<dontCareRounds;dc++)
        {
            int dcAux = dc;

            //decide which 'don't care' is '1' and which is '0'
            resetBlocksBeforeFor();
            for(i=0;i<dontCares.length;i++)
            {
                if((dcAux & 1) == 1)
                {
                    System.out.println("DC: Pun pe 1: "+dontCares[i]);
                    CelulaKarnaugh ck=new CelulaKarnaugh(1,dontCares[i]);
		    blocuri.add(ck);
                    //vezi ca dupa aia (la sfarcshitul ciclului) trebuie sa le SCOTI pe astea!
                }
                else
                {
                    System.out.println("DC: Pun pe 0: "+dontCares[i]);
                }
                dcAux = dcAux >> 1;
            }
             
            System.out.println("DON\'T CARE COMBINATION: "+dc);

            last = 0;
            j = 0;
            for(size=1;size<=Math.pow(2, nrEl);size*=2)//era size<=nrEl
            {
                    for(i=last;i<blocuri.size();i++)
                    {
                        //System.out.println("i="+i+" DIM="+blocuri.get(i).getNumberOfElements()+" expected: "+size);
                        if(blocuri.get(i).getNumberOfElements()>size)
                            break;
                        if(blocuri.get(i).getNumberOfElements()!=size)
                            continue;
                        last=i;
                        initialSize=blocuri.size();
                            for(j=i;j<initialSize;j++)
                            {
                                totalIteratii++;
                                if(blocuri.get(j).getNumberOfElements()>size)//netestat
                                        break;
                                if(i!=j)
                                {
                                    CelulaKarnaugh ck1=blocuri.get(i);
                                    CelulaKarnaugh ck2=blocuri.get(j);
                                    if(ck1.getNumberOfElements()==ck2.getNumberOfElements()
                                                    && ck1.getNumberOfElements()==size)
                                    {

                                        if(ck1.getNumberOfElements()==1)
                                        {//sa nu dea Dracu sa le unesti cu &&
                                            if(CelulaKarnaugh.isJoinableSingleVariable(ck1, ck2))
                                            {
                                                ckNoua=ck1.join(ck2);
                                                blocuri.add(ckNoua);
                                            }
                                        }
                                        else if(CelulaKarnaugh.isJoinableMultiVariable(ck1, ck2))
                                        {
                                            ckNoua=ck1.join(ck2);
                                            if(Minimizer.containsDuplicate(ckNoua, blocuri)==false)
                                                blocuri.add(ckNoua);
                                        }
                                    }
                                }//end if(i!=j)
                               }//end for
                }//end for size(inner)
            }//end for size (outer)
            System.out.println("Cacat bun: "+goodShit+" cacat rau"+badShit);
            removeDuplicates(blocuri);

            //NU SCOATE ASTA!
            for(i=0;i<blocuri.size();i++)
            {
                    if(removeIfInLargerCells(i,blocuri.get(i),blocuri))
                            i--;
                    if(i<-1)
                            i=0;//just to be safe
            }

         removeUselessCrosses();

         int sum=0;
         for(i=0;i<blocuri.size();i++)
             sum+=CelulaKarnaugh.countTerms(blocuri.get(i).minTerm(fromMap));
         
         if(min>blocuri.size())
         {
             min=blocuri.size();
             numberOfTerms = sum;
             System.out.println("BEST FUCKING SOLUTION(1)");
             //iti trebuie cumva copie deep?
             solutionBlocks = new ArrayList<CelulaKarnaugh>(blocuri);
         }
         else if(min==blocuri.size())
         {
             //the expressions have the same number of factors
             //so calculate the longest one by adding the length of the factors
             if(sum<numberOfTerms)
             {
              numberOfTerms = sum;
              System.out.println("BEST FUCKING SOLUTION(2)");
              //iti trebuie cumva copie deep?
              solutionBlocks = new ArrayList<CelulaKarnaugh>(blocuri);
             }
         }
         System.out.println("LABAGIULE, am "+blocuri.size()+" coaie; minim=" +min+" N.O.T. = "+numberOfTerms);
         System.out.println("..................................");
         for(i=0;i<blocuri.size();i++)
            System.out.println(blocuri.get(i).toString());
         System.out.println("..................................");

        }//end for dont care rounds
	
        //fetch solution blocks again:
        blocuri=new ArrayList<CelulaKarnaugh>(solutionBlocks);

	System.out.println("*******************************\nBlocurile sunt:");
	for(i=0;i<blocuri.size();i++)
	{
		Vector<Atom> minTerm;
		System.out.println("-----------------------");
		System.out.println(blocuri.get(i).toString());
		System.out.println("MINTERM: "+blocuri.get(i).minTerm(fromMap));
		
		minTerm=blocuri.get(i).minTerm(fromMap);
		//transfer from minterm to rsult:
		for(j=0;j<minTerm.size();j++)
			result.add(minTerm.get(j));
		result.add(new Atom("OR",Tip.OPERATOR));
		System.out.println("szie="+result.size());
	}
	/*removeDuplicates(blocuri);//!ASTA E IN PLUS; poate tre sa il scoti
        removeUselessCrosses();//si asat e in plus!!*/
	if(result.size()>0)
		result.remove(result.size()-1);//remove last "OR" operator
        if(tabel.length==4)//cu 2 variabile: 0 1 3 2
        {
            int aux=tabel[2];
            tabel[2]=tabel[3];
            tabel[3]=aux;
        }
	return result;	
}

}
