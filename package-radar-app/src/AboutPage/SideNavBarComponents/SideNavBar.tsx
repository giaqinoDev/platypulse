import { Link } from "react-router";
import { MdOutlineAutoAwesomeMosaic } from "react-icons/md";
import { LuWrench } from "react-icons/lu";
import { MdOutlineGroups } from "react-icons/md";
import { FaShareNodes } from "react-icons/fa6";



import platyPulse_svg from "../../../public/Platypulse_logo.svg"

import "../SideNavBarComponents/SideNavBar.css"
import { useEffect, useRef } from "react";
function SideNavBar(){
    const overViewButtonRef = useRef(null);
    //const {activeButton, setActiveButton} = useState(null);

    useEffect(() => {
        if(overViewButtonRef.current){
            //setActiveButton(overViewButtonRef.current);
            //overViewButtonRef.current.classList.add("active");
        }
    }, []);
    return(
        <div className="side-nav-bar-root">
            <div className="side-bar-header">
                <Link to="/home">
                    <img src={platyPulse_svg} className="platy-pulse-svg"></img>
                </Link>
                <div className="side-bar-header-body">
                    <h2 style={{"margin": "10px 0px"}}>Platy Pulse Reports</h2>
                    <p style={{"margin": "0px"}}>v1.0.0</p>
                </div>
            </div>

            <a className="side-nav-bar-button active-button" ref={overViewButtonRef} href="#overview-section">
                <MdOutlineAutoAwesomeMosaic className="button-icon"/>
                <p className="button-title">Overview</p>
            </a>

            <a className="side-nav-bar-button" href="#community-section">
                <MdOutlineGroups className="button-icon"/>
                <p className="button-title">Community</p>
            </a>
            
            <a className="side-nav-bar-button" href="#maintenance-section">
                <LuWrench className="button-icon"/>
                <p className="button-title">Maintenance</p>
            </a>

            <a className="side-nav-bar-button" href="#dependencies-section">
                <FaShareNodes className="button-icon"/>
                <p className="button-title">Dependencies</p>
            </a>

        </div>
    );
}

export default SideNavBar;