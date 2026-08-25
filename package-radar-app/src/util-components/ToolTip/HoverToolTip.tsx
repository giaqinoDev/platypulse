import "./HoverToolTip.css"
import { useState, useRef, useEffect, forwardRef, type ReactNode } from "react";
// icon: Icon -> gets the component type (the actual definition as a function, not the JSX)
/* eslint-disable-next-line no-unused-vars */ //removes linter warning that pops u pdue to "Icon"

interface HoverToolTipProps{
    icon?: string|null,
    scale?: string|null,
    content?: string|null,
    hoverColor?: string|null,
    children?: ReactNode|null,
    onPress?: () => void
}
export const HoverToolTip = forwardRef(({ icon , scale, content, hoverColor, children, onPress}: HoverToolTipProps, ref) => {
    //forwardRef is not needed but kept in cases of future requirement
    const [showToolTip, setShowToolTip] = useState<boolean>(false)
    const [yOffset, setYOffset] = useState<number>(0);
    const toolTipContainer = useRef<HTMLDivElement>(null);

    useEffect(() =>{
        if(toolTipContainer.current){
            setYOffset(toolTipContainer.current.getBoundingClientRect().height);
        }
    }, [showToolTip])

    return(
        <div ref={toolTipContainer} className="toolTip-container" style={{"--hover-color": hoverColor}as React.CSSProperties} 
            onMouseEnter={() => {if(content){setShowToolTip(true)}}} 
            onMouseLeave={() => {if(content){setShowToolTip(false)}}} 
            onClick={()=>{if(onPress != null){onPress()} setShowToolTip(false) }}
        >

            {icon && <img src={icon} style={{"--icon-scale": scale} as React.CSSProperties} className="toolTip-icon"/>} {/*Renders the component type icon as an element*/}
            {showToolTip && <div style={{"--offset-Y": yOffset +  "px"} as React.CSSProperties}className="toolTip-content"><p style={{margin: 0, fontSize: "0.9rem"}}>{content}</p></div>}
            {children && 
                <div style={{"--offset-Y": yOffset +  "px"} as React.CSSProperties} className="options-window">
                    {children}
                </div>
            }  
        </div>
    );
})