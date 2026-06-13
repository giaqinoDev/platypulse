import { Link } from "react-router";
import platyPulse_svg from "../../../public/Platypulse_logo.svg"
import "./MenuHeader.css"

function MenuHeader(){
    return(
        <div className="menu-header-container">
            <Link to="/home">
                <img src={platyPulse_svg} className="platy-pulse-svg"></img>
            </Link>
            <div className="nav-section">
                <Link to="/home" className="nav-button">
                    <p>Home</p>
                </Link>
                <Link to="" className="nav-button">
                    <p>About</p>
                </Link>
            </div>
        </div>
    );
}

export default MenuHeader;