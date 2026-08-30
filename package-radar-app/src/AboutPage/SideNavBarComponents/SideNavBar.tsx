import { Link, useLocation } from "react-router";
import { MdOutlineAutoAwesomeMosaic } from "react-icons/md";
import { LuWrench } from "react-icons/lu";
import { MdOutlineGroups } from "react-icons/md";
import { FaShareNodes } from "react-icons/fa6";



import platyPulse_svg from "../../../public/Platypulse_logo.svg"

import "../SideNavBarComponents/SideNavBar.css"
import { useEffect, useRef, useState } from "react";
function SideNavBar(){
    const [activeSection, setActiveSection] = useState<HTMLAnchorElement|null>(null);

    const overviewRef = useRef<HTMLAnchorElement|null>(null);
    const communityRef = useRef<HTMLAnchorElement|null>(null);
    const maintenanceRef = useRef<HTMLAnchorElement|null>(null);
    const dependenciesRef = useRef<HTMLAnchorElement|null>(null);

    // const {ecosystem, scope, packageName} = useParams();
    const location = useLocation();
    //const {activeButton, setActiveButton} = useState(null);

    function handleSection(event: React.MouseEvent<HTMLAnchorElement, MouseEvent>){
        activeSection?.classList.remove("active-section");
        setActiveSection(event.currentTarget);
    }

    useEffect(() => {
        const section = location.hash;
        if(section == "#overview-section"){
            setActiveSection(overviewRef.current);
        }
        else if(section == "#community-section"){
            setActiveSection(communityRef.current);
        }
        else if(section == "#maintenance-section"){
            setActiveSection(maintenanceRef.current);
        }
        else if(section == "#dependencies-section"){
            setActiveSection(dependenciesRef.current);
        }

    }, []);

    useEffect(() => {
        if(activeSection){
            activeSection.classList.add("active-section");
        }
    }, [activeSection]);
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

            <a className="side-nav-bar-button" 
                ref={overviewRef} 
                href="#overview-section"
                onClick={handleSection}>
                <MdOutlineAutoAwesomeMosaic className="button-icon"/>
                <p className="button-title">Overview</p>
            </a>

            <a className="side-nav-bar-button" 
                ref={communityRef}
                href="#community-section"
                onClick={handleSection}>
                <MdOutlineGroups className="button-icon"/>
                <p className="button-title">Community</p>
            </a>
            
            <a className="side-nav-bar-button"
                ref={maintenanceRef}
                href="#maintenance-section"
                onClick={handleSection}>
                <LuWrench className="button-icon"/>
                <p className="button-title">Maintenance</p>
            </a>

            <a className="side-nav-bar-button" 
                ref={dependenciesRef}
                href="#dependencies-section"
                onClick={handleSection}>
                <FaShareNodes className="button-icon"/>
                <p className="button-title">Dependencies</p>
            </a>

        </div>
    );
}

export default SideNavBar;