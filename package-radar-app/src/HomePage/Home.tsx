import "@fontsource/geist-sans/400.css";
import "@fontsource/geist-sans/200.css";

import "./Home.css";
import SearchBar from "../util-components/SearchBarComponents/SearchBar";
import SplashGraphgic from "../util-components/HomePageSplashGraphic/SplashGraphic";
import DetailsGrid from "../util-components/HomePageDetailsGrid/DetailsGrid";
import MenuHeader from "../util-components/MenuHeader/MenuHeader";

function Home(){
    
    return(
        <div>
            <MenuHeader/>
            <div className="home-page-header">
                <div className="center-header">
                    <div className="home-page-hero-section">
                        <h1 className="title">Make dependency decisions backed by <span className="highlight-word">signals</span>, not guesswork.</h1>
                        <p className="header">
                            Monitor the health of packages across the NPM ecosystem and retrieve up to date reporting to guide decisions on package inclusion.
                        </p>
                        
                        <SearchBar/>
                    </div>
                    
                    <SplashGraphgic/>
                </div>
            </div>
            <div style={{"display": "flex", "justifyContent": "center", "padding": "40px 0px"}}>
                <DetailsGrid/>
            </div>   
            
        </div>
    );
}

export default Home;