/* This file was generated by SableCC (http://www.sablecc.org/). */

package jw.jzbot.eval.jexec.node;

import jw.jzbot.eval.jexec.analysis.*;

@SuppressWarnings("nls")
public final class AInMulp extends PMulp
{
    private PMulp _first_;
    private TMult _mult_;
    private PDivp _second_;

    public AInMulp()
    {
        // Constructor
    }

    public AInMulp(
        @SuppressWarnings("hiding") PMulp _first_,
        @SuppressWarnings("hiding") TMult _mult_,
        @SuppressWarnings("hiding") PDivp _second_)
    {
        // Constructor
        setFirst(_first_);

        setMult(_mult_);

        setSecond(_second_);

    }

    @Override
    public Object clone()
    {
        return new AInMulp(
            cloneNode(this._first_),
            cloneNode(this._mult_),
            cloneNode(this._second_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAInMulp(this);
    }

    public PMulp getFirst()
    {
        return this._first_;
    }

    public void setFirst(PMulp node)
    {
        if(this._first_ != null)
        {
            this._first_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._first_ = node;
    }

    public TMult getMult()
    {
        return this._mult_;
    }

    public void setMult(TMult node)
    {
        if(this._mult_ != null)
        {
            this._mult_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._mult_ = node;
    }

    public PDivp getSecond()
    {
        return this._second_;
    }

    public void setSecond(PDivp node)
    {
        if(this._second_ != null)
        {
            this._second_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._second_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._first_)
            + toString(this._mult_)
            + toString(this._second_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._first_ == child)
        {
            this._first_ = null;
            return;
        }

        if(this._mult_ == child)
        {
            this._mult_ = null;
            return;
        }

        if(this._second_ == child)
        {
            this._second_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._first_ == oldChild)
        {
            setFirst((PMulp) newChild);
            return;
        }

        if(this._mult_ == oldChild)
        {
            setMult((TMult) newChild);
            return;
        }

        if(this._second_ == oldChild)
        {
            setSecond((PDivp) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}