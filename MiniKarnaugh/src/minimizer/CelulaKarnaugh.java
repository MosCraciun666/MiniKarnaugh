package minimizer;
import java.util.ArrayList;
import java.util.Vector;

import stiva.StackProcessor;

import exceptii.*;
import atomi.Atom;
import atomi.Tip;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Serban
 */
public class CelulaKarnaugh
{
    private int nrElemente;
    private int adresa;
    private ArrayList<CelulaKarnaugh> componente;
    private static Vector<Atom> variabile;
    
    public static void setVariables(Vector<Atom> _variabile)
    {
    	variabile=_variabile;
    }
    
    public CelulaKarnaugh(int nrElemente,int adresa)
    {
        this.nrElemente=nrElemente;
        this.adresa=adresa;
        componente=new ArrayList<CelulaKarnaugh>();
        //variabile=new Vector<Atom>();
    }
    public void baga(CelulaKarnaugh ck)
    {
        componente.add(ck);
    }
    
    
    public static boolean atLeastOneCommonElement(CelulaKarnaugh ck1,CelulaKarnaugh ck2)
    {
    	int i,j;
    	if(ck1.getNumberOfElements()==1 && ck2.getNumberOfElements()==1)
    	{
    		if(ck1.adresa==ck2.adresa) return true;
    		return false;
    	}
    	for(i=0;i<ck1.getNumberOfElementsForReal();i++)
    		for(j=0;j<ck2.getNumberOfElementsForReal();j++)
    			if(ck1.componente.get(i).adresa==ck2.componente.get(j).adresa)
    				return true;
    	return false;
    }
    
    public static boolean isJoinableSingleVariable(CelulaKarnaugh ck1,CelulaKarnaugh ck2) throws TooManyVariablesException
    {
        int rezultatXOR=ck1.adresa^ck2.adresa;
        int i,bitiDiferiti=0;
        /*if(ck1.nrElemente!=1) 
        	{
        	System.out.println("Firia dreacu tu cu ma-ta astazi si maine!");
        	throw new TooManyVariablesException();        	
        	}
        if(ck2.nrElemente!=1) throw new TooManyVariablesException();*/
        
        for(i=0;i<32;i++)
        {
            if((rezultatXOR&1)==1)//bit diferit
                bitiDiferiti++;
            rezultatXOR=rezultatXOR>>1;
        }
        if(bitiDiferiti==1)   return true;
        return false;
    }
    

    
    
  /*NOUA*/ public static boolean isJoinableMultiVariable(CelulaKarnaugh ck1,CelulaKarnaugh ck2) throws TooManyVariablesException
    {
    	int i,j;
    	boolean gasit;
    	if(ck1.nrElemente!=ck2.nrElemente)
    		return false;
    	if(CelulaKarnaugh.atLeastOneCommonElement(ck1, ck2)) 
    		return false;
    	for(i=0;i<ck1.nrElemente;i++)
    	{
    		gasit=false;
    		for(j=0;j<ck2.nrElemente;j++)
    		{
    			try
    			{
	    			if(isJoinableSingleVariable(ck1.componente.get(i),ck2.componente.get(j)))
	    			{
	    				//System.out.println("ok");
	    				gasit=true;
	    				break;
	    			}
    			}
    			catch(TooManyVariablesException e)
    			{
    				//ignore; error is not relevant here
    			}
    			
    		}
    		if(!gasit) {/*System.out.println("Nok");*/return false;}
    	}
    	return true;
    }
    
    
    public int getNumberOfElements() {return nrElemente;}
    public int getNumberOfElementsForReal()
    {
    	return componente.size();
    }
    
    
    public static int countTerms(Vector<Atom> v)
    {
        int i;
        int count = 0;
        for(i=0;i<v.size();i++)
            if(v.get(i).typeEquals(Tip.VARIABILA))
                count++;
        return count;
    }
    
    public Vector<Atom> minTerm(boolean fromMap)
    {
    	StringBuffer luaMiAiCoaieleNGura=new StringBuffer();
    	Vector<Atom> result=new Vector<Atom>();
	int[] adresa=new int[nrElemente];
	int i,bit=0,lastBit=0;
	boolean egal=false;

        //if necessary (minimize from k-map and at least 5 variables), switch the order
        //reason is:
        /*
         * A B \ C D E 000 001 011 010 100 101 111 110
         * 00
         * 01
         * in this case, the weigths of the variables to the composition of the address are:
         * c 16, a 8, b 4, d 2, e 1
         */
        int[] orderLessThan5={0,1,2,3};//ok
        int[] order5={0,1,3,4,2};//ok
        int[] order6={0,1,3,4,2,5};//ok
        int[] order7={0,1,4,5,2,3,6};//ok
        int[] order8={0,1,4,5,2,3,6,7};//ok
        int[] order;


        switch(variabile.size())
        {
            case 5: order=order5; break;
            case 6: order=order6; break;
            case 7: order=order7; break;
            case 8: order=order8; break;
            default: 
                order=orderLessThan5; //stupid assumption
            break;
        }

	if(nrElemente!=1)
		for(i=0;i<nrElemente;i++)
			adresa[i]=componente.get(i).adresa;
	else adresa[0]=this.adresa;

    	for(bit=0;bit<StackProcessor.getNumberOfDistinctVariables();bit++)//aici in loc de treijdoi pui (nr global de variabile)
    	{
    		egal=true;
	    	for(i=0;i<nrElemente;i++)
	    	{
	    		if(i==0) lastBit=adresa[i]&1;
	    		if((adresa[i]&1)!=lastBit) 
	    		{
	    			egal=false;
	    			//sa nu dea Dracu sa pui ce e jos:
		    		//adresa[i]=adresa[i]>>1;//vezi ca nu se duce asa, bai pulocule
	    			//break;
	    		}
	    		lastBit=adresa[i]&1;
	    		adresa[i]=adresa[i]>>1;
	    	}
	    	if(egal)
	    	{	    		
	    		String vname=null;
	    		if(bit<variabile.size())
	    		{
                            if(!fromMap)
	    			vname=variabile.get(bit).getName();
                            else vname=variabile.get(order[bit]).getName();
                            luaMiAiCoaieleNGura.append("v-name: ");
                            luaMiAiCoaieleNGura.append(vname);
                            if(!fromMap)
	    			result.add(variabile.get(bit));
                            else result.add(variabile.get(order[bit]));
	    		}
	    		
	    		luaMiAiCoaieleNGura.append("bit");
	    		luaMiAiCoaieleNGura.append(bit);
	    		luaMiAiCoaieleNGura.append("=");
	    		if(lastBit==1) luaMiAiCoaieleNGura.append("1. ");
	    		else 
	    		{
	    			luaMiAiCoaieleNGura.append("0. ");
	    			result.add(new Atom(".",Tip.OPERATOR));
	    		}
	    		result.add(new Atom("AND",Tip.OPERATOR));
	    	}
    	}
    	if(result.size()>=2)
    		result.remove(result.size()-1);
    	System.out.println("lua-mi-ai coaiele-n gura="+luaMiAiCoaieleNGura.toString());
    	
    	return result;
    }
    
    
    
    
    
    private void normalize()
    {
    	int i;
    	for(i=0;i<componente.size();i++)
    		componente.get(i).nrElemente=1;
    }
    
    
    
      
    
    
    public CelulaKarnaugh join(CelulaKarnaugh ck) throws DifferentSizesException
    {
    	int i;
    	CelulaKarnaugh ckResult=new CelulaKarnaugh(0,0);
    	ckResult.adresa=this.adresa;//nu stiu de ce plm mai fac asta, da sa fiu sigur, totusi
    	ckResult.nrElemente=ck.nrElemente+this.nrElemente;
    	
    	if(nrElemente!=ck.nrElemente) throw new DifferentSizesException();
    	if(nrElemente!=1)
    	{
    	for(i=0;i<ck.nrElemente;i++)
    		///componente.add(ck.componente.get(i));
    		ckResult.componente.add(ck.componente.get(i));
    	for(i=0;i<this.nrElemente;i++)
    		ckResult.componente.add(this.componente.get(i));
    		//baga(ck.componente.get(i));
    	}
    	else
    	{
    		/**baga(new CelulaKarnaugh(1,this.adresa));
    		baga(ck);
    		nrElemente++;
    		ck.nrElemente=0;*/
    		ckResult.nrElemente=2;
    		ckResult.componente.add(this);
    		ckResult.componente.add(ck);
    	}
    	//ck.componente.clear();
    	///nrElemente+=ck.nrElemente;
    	//ck.nrElemente=0;
    	normalize();//???
    	
    	return ckResult;
    }
    
    
    
    
    
    
    
    
    @Override
    public String toString()
    {
    	int i;
        StringBuffer resBuffer=new StringBuffer();
        resBuffer.append("Adresa ");
        resBuffer.append(adresa);
        resBuffer.append("\n");
        resBuffer.append("Nr. elemente: ");
        resBuffer.append(nrElemente);
        resBuffer.append("\n");
    	/*if(nrElemente!=1)
    		for(i=0;i<nrElemente;i++)
    			res+=componente.get(i).adresa+"\n";*/
    	for(i=0;i<componente.size();i++)
        {
         resBuffer.append(componente.get(i).adresa);
         resBuffer.append("\n");
        }
    	return resBuffer.toString();
    	
    }
    
    public boolean contains(CelulaKarnaugh ck)
    {
    	int i;
    	for(i=0;i<componente.size();i++)
    		if(componente.get(i).adresa==ck.adresa)
    			return true;
    	return false;
    }
    public CelulaKarnaugh getElement(int i){return componente.get(i);}
    
    public static boolean exactSameElements(CelulaKarnaugh ck1,CelulaKarnaugh ck2)
    {
    	int i,j;
    	boolean found;
    	if(ck1.componente.size()!=ck2.componente.size())
    		return false;
    	for(i=0;i<ck1.componente.size();i++)
    	{
    		found=false;
    		for(j=0;j<ck2.componente.size();j++)
    		{
    			if(ck1.componente.get(i).adresa==ck2.componente.get(j).adresa)
    				found=true;
    		}
    		if(!found) return false;
    	}
    	return true;
    }
    
    public int getAddress(){return adresa;}
    public boolean containsInt(int x)
    {
        int i;
        for(i=0;i<nrElemente;i++)
            if(x==componente.get(i).getAddress())
                return true;
        return false;
    }

    public static boolean exactSameElementsIgnoreLength(CelulaKarnaugh ck1,CelulaKarnaugh ck2)
    {
    	int i,j;
    	boolean found;
        if(ck1.getNumberOfElements()==1 && ck2.getNumberOfElements()==1)
        {
            if(ck1.getAddress()==ck2.getAddress())
                return true;
            return false;
        }
        if(ck1.getNumberOfElements()==1 && ck2.getNumberOfElements()!=1)
        {
            for(i=0;i<ck2.getNumberOfElements();i++)
                if(ck2.getElement(i).getAddress()/*sigur??*/==ck1.getAddress())
                    return true;
            return false;
        }
        if(ck2.getNumberOfElements()==1 && ck1.getNumberOfElements()!=1)
        {
            for(i=0;i<ck1.getNumberOfElements();i++)
                if(ck1.getElement(i).getAddress()/*sigur??*/==ck2.getAddress())
                    return true;
            return false;
        }

//else: multiple shit both sides
    	for(i=0;i<ck1.componente.size();i++)
    	{
    		found=false;
    		for(j=0;j<ck2.componente.size();j++)
    		{
    			if(ck1.componente.get(i).adresa==ck2.componente.get(j).adresa)
    				found=true;
    		}
    		if(!found) return false;
    	}
    	return true;
    }
    
}
